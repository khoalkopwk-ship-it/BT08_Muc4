IF DB_ID(N'BT08_SPRING_BOOT') IS NULL
BEGIN
    CREATE DATABASE BT08_SPRING_BOOT;
    PRINT N'Đã tạo database BT08_SPRING_BOOT.';
END
ELSE
BEGIN
    PRINT N'Database BT08_SPRING_BOOT đã tồn tại.';
END;
GO

USE BT08_SPRING_BOOT;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

/* ============================================================================
   1. TẠO/NÂNG CẤP SCHEMA ĐÍCH
   ============================================================================ */

IF OBJECT_ID(N'dbo.Categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Categories
    (
        category_id BIGINT IDENTITY(1,1) NOT NULL,
        category_name NVARCHAR(100) NOT NULL,
        icon NVARCHAR(255) NULL,
        status INT NOT NULL CONSTRAINT DF_Categories_Status DEFAULT 1,

        CONSTRAINT PK_Categories PRIMARY KEY(category_id),
        CONSTRAINT UQ_Categories_CategoryName UNIQUE(category_name),
        CONSTRAINT CK_Categories_Status CHECK(status IN (0, 1))
    );

    PRINT N'Đã tạo bảng dbo.Categories.';
END;
GO

IF COL_LENGTH(N'dbo.Categories', N'icon') IS NULL
BEGIN
    ALTER TABLE dbo.Categories ADD icon NVARCHAR(255) NULL;
END;
GO

IF COL_LENGTH(N'dbo.Categories', N'status') IS NULL
BEGIN
    ALTER TABLE dbo.Categories
    ADD status INT NOT NULL
        CONSTRAINT DF_Categories_Status_Migration DEFAULT 1;
END;
GO

IF OBJECT_ID(N'dbo.Users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        username VARCHAR(50) NOT NULL,
        password VARCHAR(255) NOT NULL,
        fullname NVARCHAR(100) NOT NULL,
        email VARCHAR(100) NOT NULL,
        phone VARCHAR(20) NULL,
        images NVARCHAR(255) NULL,
        role VARCHAR(20) NOT NULL CONSTRAINT DF_Users_Role DEFAULT 'USER',
        enabled BIT NOT NULL CONSTRAINT DF_Users_Enabled DEFAULT 1,
        created_date DATETIME2 NOT NULL
            CONSTRAINT DF_Users_CreatedDate DEFAULT SYSDATETIME(),

        CONSTRAINT PK_Users PRIMARY KEY(id),
        CONSTRAINT UQ_Users_Username UNIQUE(username),
        CONSTRAINT UQ_Users_Email UNIQUE(email),
        CONSTRAINT CK_Users_Role CHECK(role IN ('ADMIN', 'USER'))
    );

    PRINT N'Đã tạo bảng dbo.Users.';
END;
GO

IF COL_LENGTH(N'dbo.Users', N'phone') IS NULL
BEGIN
    ALTER TABLE dbo.Users ADD phone VARCHAR(20) NULL;
END;
GO

IF COL_LENGTH(N'dbo.Users', N'images') IS NULL
BEGIN
    ALTER TABLE dbo.Users ADD images NVARCHAR(255) NULL;
END;
GO

IF COL_LENGTH(N'dbo.Users', N'role') IS NULL
BEGIN
    ALTER TABLE dbo.Users
    ADD role VARCHAR(20) NOT NULL
        CONSTRAINT DF_Users_Role_Migration DEFAULT 'USER';
END;
GO

IF COL_LENGTH(N'dbo.Users', N'enabled') IS NULL
BEGIN
    ALTER TABLE dbo.Users
    ADD enabled BIT NOT NULL
        CONSTRAINT DF_Users_Enabled_Migration DEFAULT 1;
END;
GO

IF COL_LENGTH(N'dbo.Users', N'created_date') IS NULL
BEGIN
    ALTER TABLE dbo.Users
    ADD created_date DATETIME2 NOT NULL
        CONSTRAINT DF_Users_CreatedDate_Migration DEFAULT SYSDATETIME();
END;
GO

IF OBJECT_ID(N'dbo.Products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Products
    (
        product_id BIGINT IDENTITY(1,1) NOT NULL,
        product_name NVARCHAR(255) NOT NULL,
        price DECIMAL(18,2) NOT NULL,
        description NVARCHAR(MAX) NULL,
        image NVARCHAR(255) NULL,
        created_date DATETIME2 NOT NULL
            CONSTRAINT DF_Products_CreatedDate DEFAULT SYSDATETIME(),
        category_id BIGINT NOT NULL,

        CONSTRAINT PK_Products PRIMARY KEY(product_id),
        CONSTRAINT FK_Products_Categories
            FOREIGN KEY(category_id) REFERENCES dbo.Categories(category_id),
        CONSTRAINT CK_Products_Price CHECK(price >= 0)
    );

    PRINT N'Đã tạo bảng dbo.Products.';
END;
GO

/* Không âm thầm dùng bảng Products sai schema. */
IF COL_LENGTH(N'dbo.Products', N'category_id') IS NULL
BEGIN
    THROW 51001,
        N'Bảng BT08_SPRING_BOOT.dbo.Products đang dùng schema cũ: thiếu category_id. Hãy đổi tên bảng cũ trước khi chạy lại.',
        1;
END;
GO

IF COL_LENGTH(N'dbo.Products', N'created_date') IS NULL
BEGIN
    THROW 51002,
        N'Bảng BT08_SPRING_BOOT.dbo.Products đang dùng schema cũ: thiếu created_date. Hãy đổi tên bảng cũ trước khi chạy lại.',
        1;
END;
GO

/* ============================================================================
   2. CATEGORY CHUẨN CỦA DỰ ÁN MỚI
   ============================================================================ */

IF NOT EXISTS (
    SELECT 1 FROM dbo.Categories WHERE category_name = N'Quần áo nam'
)
BEGIN
    INSERT dbo.Categories(category_name, icon, status)
    VALUES(N'Quần áo nam', N'images/category/ao-nam.png', 1);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM dbo.Categories WHERE category_name = N'Quần áo nữ'
)
BEGIN
    INSERT dbo.Categories(category_name, icon, status)
    VALUES(N'Quần áo nữ', N'images/category/ao-nu.png', 1);
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM dbo.Categories WHERE category_name = N'Giày dép'
)
BEGIN
    INSERT dbo.Categories(category_name, icon, status)
    VALUES(N'Giày dép', N'images/category/giay-dep.png', 1);
END;
GO

/* ============================================================================
   3. DI CHUYỂN DỮ LIỆU TỪ BT02_CRUD_JPA NẾU DATABASE CŨ TỒN TẠI
   Dùng dynamic SQL để script vẫn chạy được trên máy không có database cũ.
   ============================================================================ */

IF DB_ID(N'BT02_CRUD_JPA') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Category', N'U') IS NOT NULL
BEGIN
    EXEC sys.sp_executesql N'
        INSERT INTO dbo.Categories(category_name, icon, status)
        SELECT
            LTRIM(RTRIM(oldCategory.cate_name)),
            CASE
                WHEN NULLIF(LTRIM(RTRIM(oldCategory.icons)), N'''') IS NULL
                    THEN NULL
                WHEN REPLACE(LTRIM(RTRIM(oldCategory.icons)), N''\'', N''/'') LIKE N''images/%''
                  OR REPLACE(LTRIM(RTRIM(oldCategory.icons)), N''\'', N''/'') LIKE N''uploads/%''
                    THEN REPLACE(LTRIM(RTRIM(oldCategory.icons)), N''\'', N''/'')
                ELSE N''images/''
                     + REPLACE(LTRIM(RTRIM(oldCategory.icons)), N''\'', N''/'')
            END,
            CASE WHEN oldCategory.status = 0 THEN 0 ELSE 1 END
        FROM BT02_CRUD_JPA.dbo.Category AS oldCategory
        WHERE NULLIF(LTRIM(RTRIM(oldCategory.cate_name)), N'''') IS NOT NULL
          AND LEN(LTRIM(RTRIM(oldCategory.cate_name))) <= 100
          AND NOT EXISTS
          (
              SELECT 1
              FROM dbo.Categories AS currentCategory
              WHERE currentCategory.category_name =
                    LTRIM(RTRIM(oldCategory.cate_name))
          );
    ';

    PRINT N'Đã chuyển các Category hợp lệ từ BT02_CRUD_JPA.';
END
ELSE
BEGIN
    PRINT N'Không có BT02_CRUD_JPA.dbo.Category; bỏ qua migration Category.';
END;
GO

IF DB_ID(N'BT02_CRUD_JPA') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Users', N'U') IS NOT NULL
BEGIN
    EXEC sys.sp_executesql N'
        INSERT INTO dbo.Users
        (
            username,
            password,
            fullname,
            email,
            phone,
            images,
            role,
            enabled,
            created_date
        )
        SELECT
            CONVERT(VARCHAR(50), LTRIM(RTRIM(oldUser.username))),
            CONVERT(VARCHAR(255), oldUser.password),
            CONVERT(NVARCHAR(100), LTRIM(RTRIM(oldUser.fullname))),
            CONVERT(VARCHAR(100), LOWER(LTRIM(RTRIM(oldUser.email)))),
            CASE
                WHEN NULLIF(LTRIM(RTRIM(oldUser.phone)), N'''') IS NULL THEN NULL
                ELSE CONVERT(VARCHAR(20), LTRIM(RTRIM(oldUser.phone)))
            END,
            CASE
                WHEN NULLIF(LTRIM(RTRIM(oldUser.images)), N'''') IS NULL
                    THEN N''images/admin.png''
                WHEN REPLACE(LTRIM(RTRIM(oldUser.images)), N''\'', N''/'') LIKE N''images/%''
                  OR REPLACE(LTRIM(RTRIM(oldUser.images)), N''\'', N''/'') LIKE N''uploads/%''
                    THEN REPLACE(LTRIM(RTRIM(oldUser.images)), N''\'', N''/'')
                ELSE N''images/''
                     + REPLACE(LTRIM(RTRIM(oldUser.images)), N''\'', N''/'')
            END,
            CASE WHEN UPPER(oldUser.role) = ''ADMIN'' THEN ''ADMIN'' ELSE ''USER'' END,
            CASE
                WHEN oldUser.status = 1
                 AND (
                        CONVERT(VARCHAR(255), oldUser.password) LIKE ''$2a$%''
                     OR CONVERT(VARCHAR(255), oldUser.password) LIKE ''$2b$%''
                     OR CONVERT(VARCHAR(255), oldUser.password) LIKE ''$2y$%''
                 )
                    THEN CONVERT(BIT, 1)
                ELSE CONVERT(BIT, 0)
            END,
            COALESCE(oldUser.createdDate, SYSDATETIME())
        FROM BT02_CRUD_JPA.dbo.Users AS oldUser
        WHERE NULLIF(LTRIM(RTRIM(oldUser.username)), N'''') IS NOT NULL
          AND NULLIF(LTRIM(RTRIM(oldUser.email)), N'''') IS NOT NULL
          AND NULLIF(LTRIM(RTRIM(oldUser.fullname)), N'''') IS NOT NULL
          AND oldUser.password IS NOT NULL
          AND LEN(LTRIM(RTRIM(oldUser.username))) <= 50
          AND LEN(LTRIM(RTRIM(oldUser.email))) <= 100
          AND LEN(LTRIM(RTRIM(oldUser.fullname))) <= 100
          AND LEN(oldUser.password) <= 255

          /* Để DataInitializer của Spring Boot tạo admin BCrypt mặc định. */
          AND LOWER(LTRIM(RTRIM(oldUser.username))) <> ''admin''
          AND LOWER(LTRIM(RTRIM(oldUser.email))) <> ''admin@example.com''

          AND NOT EXISTS
          (
              SELECT 1 FROM dbo.Users AS currentUser
              WHERE LOWER(currentUser.username) = LOWER(LTRIM(RTRIM(oldUser.username)))
                 OR LOWER(currentUser.email) = LOWER(LTRIM(RTRIM(oldUser.email)))
          );
    ';

    PRINT N'Đã chuyển các User hợp lệ từ BT02_CRUD_JPA.';
    PRINT N'User dùng mật khẩu cũ không phải BCrypt được đặt enabled = 0.';
END
ELSE
BEGIN
    PRINT N'Không có BT02_CRUD_JPA.dbo.Users; bỏ qua migration User.';
END;
GO

IF DB_ID(N'BT02_CRUD_JPA') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Products', N'U') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Category', N'U') IS NOT NULL
BEGIN
    EXEC sys.sp_executesql N'
        INSERT INTO dbo.Products
        (
            product_name,
            price,
            description,
            image,
            created_date,
            category_id
        )
        SELECT
            LTRIM(RTRIM(oldProduct.product_name)),
            oldProduct.price,
            oldProduct.description,
            CASE
                WHEN NULLIF(LTRIM(RTRIM(oldProduct.image)), N'''') IS NULL
                    THEN NULL
                WHEN REPLACE(LTRIM(RTRIM(oldProduct.image)), N''\'', N''/'') LIKE N''images/%''
                  OR REPLACE(LTRIM(RTRIM(oldProduct.image)), N''\'', N''/'') LIKE N''uploads/%''
                    THEN REPLACE(LTRIM(RTRIM(oldProduct.image)), N''\'', N''/'')
                ELSE N''images/''
                     + REPLACE(LTRIM(RTRIM(oldProduct.image)), N''\'', N''/'')
            END,
            COALESCE(oldProduct.createdDate, SYSDATETIME()),
            currentCategory.category_id
        FROM BT02_CRUD_JPA.dbo.Products AS oldProduct
        INNER JOIN BT02_CRUD_JPA.dbo.Category AS oldCategory
            ON oldCategory.cate_id = oldProduct.cate_id
        INNER JOIN dbo.Categories AS currentCategory
            ON currentCategory.category_name = LTRIM(RTRIM(oldCategory.cate_name))
        WHERE NULLIF(LTRIM(RTRIM(oldProduct.product_name)), N'''') IS NOT NULL
          AND oldProduct.price >= 0
          AND NOT EXISTS
          (
              SELECT 1
              FROM dbo.Products AS currentProduct
              WHERE currentProduct.product_name =
                    LTRIM(RTRIM(oldProduct.product_name))
                AND currentProduct.category_id = currentCategory.category_id
          );
    ';

    PRINT N'Đã chuyển Product từ BT02_CRUD_JPA và ánh xạ lại category_id.';
END
ELSE
BEGIN
    PRINT N'Không có bảng Product/Category cũ; bỏ qua migration Product.';
END;
GO

/* ============================================================================
   4. BỔ SUNG DỮ LIỆU MẪU CÒN THIẾU
   Nếu dữ liệu đã được migration, NOT EXISTS sẽ ngăn chèn trùng theo tên + Category.
   ============================================================================ */

INSERT INTO dbo.Products
(
    product_name,
    price,
    description,
    image,
    category_id
)
SELECT
    sample.product_name,
    sample.price,
    sample.description,
    sample.image,
    category.category_id
FROM
(
    VALUES
    (N'Áo polo nam',          CAST(299000 AS DECIMAL(18,2)), N'Áo polo nam cơ bản.',                         N'images/product/ao-polo-nam.png',       N'Quần áo nam'),
    (N'Đầm công sở nữ',       CAST(459000 AS DECIMAL(18,2)), N'Đầm công sở thanh lịch.',                    N'images/product/dam-cong-so-nu.png',    N'Quần áo nữ'),
    (N'Áo Polo Nam Basic',    CAST(249000 AS DECIMAL(18,2)), N'Áo polo cotton thoáng mát, form trẻ trung.', N'images/product/ao-polo-nam.png',       N'Quần áo nam'),
    (N'Áo Sơ Mi Nam',         CAST(329000 AS DECIMAL(18,2)), N'Áo sơ mi nam form rộng, dễ phối đồ.',        N'images/product/ao-so-mi-nam.png',      N'Quần áo nam'),
    (N'Áo Thun Nam Cotton',   CAST(179000 AS DECIMAL(18,2)), N'Áo thun nam cơ bản, chất cotton mềm.',       N'images/product/ao-thun-nam.png',       N'Quần áo nam'),
    (N'Quần Kaki Nam',        CAST(399000 AS DECIMAL(18,2)), N'Quần kaki nam trẻ trung và lịch sự.',        N'images/product/quan-kaki-nam.png',     N'Quần áo nam'),
    (N'Quần Jean Nam',        CAST(459000 AS DECIMAL(18,2)), N'Quần jean nam slim fit.',                    N'images/product/quan-jean-nam.png',     N'Quần áo nam'),
    (N'Áo Khoác Nam',         CAST(549000 AS DECIMAL(18,2)), N'Áo khoác nam phong cách tối giản.',          N'images/product/ao-khoac-nam.png',      N'Quần áo nam'),
    (N'Quần Short Nam',       CAST(269000 AS DECIMAL(18,2)), N'Quần short nam thoáng mát.',                 N'images/product/quan-short-nam.png',    N'Quần áo nam'),
    (N'Áo Thun Nữ',           CAST(189000 AS DECIMAL(18,2)), N'Áo thun nữ cổ tròn.',                        N'images/product/ao-thun-nu.png',        N'Quần áo nữ'),
    (N'Áo Công Sở Nữ',        CAST(299000 AS DECIMAL(18,2)), N'Áo nữ phong cách công sở.',                  N'images/product/ao-cong-so-nu.png',     N'Quần áo nữ'),
    (N'Váy Nữ Dáng Ngắn',     CAST(359000 AS DECIMAL(18,2)), N'Váy nữ trẻ trung.',                          N'images/product/vay-nu.png',            N'Quần áo nữ'),
    (N'Đầm Nữ Công Sở',       CAST(489000 AS DECIMAL(18,2)), N'Đầm nữ thanh lịch.',                         N'images/product/dam-cong-so-nu.png',    N'Quần áo nữ'),
    (N'Quần Jean Nữ',         CAST(429000 AS DECIMAL(18,2)), N'Quần jean nữ ống rộng.',                     N'images/product/quan-jean-nu.png',      N'Quần áo nữ'),
    (N'Áo Croptop Nữ',        CAST(219000 AS DECIMAL(18,2)), N'Áo croptop nữ trẻ trung.',                   N'images/product/ao-croptop-nu.png',     N'Quần áo nữ'),
    (N'Chân Váy Chữ A',       CAST(319000 AS DECIMAL(18,2)), N'Chân váy chữ A dễ phối.',                    N'images/product/chan-vay-chu-a.png',    N'Quần áo nữ'),
    (N'Giày Sneaker Nam',     CAST(499000 AS DECIMAL(18,2)), N'Giày sneaker nam thể thao.',                 N'images/product/giay-sneaker-nam.png', N'Giày dép'),
    (N'Giày Sneaker Nữ',      CAST(469000 AS DECIMAL(18,2)), N'Giày sneaker nữ thời trang.',                N'images/product/giay-sneaker-nu.png',  N'Giày dép'),
    (N'Sandal Nữ',            CAST(289000 AS DECIMAL(18,2)), N'Sandal nữ nhẹ và thoáng.',                   N'images/product/sandal-nu.png',         N'Giày dép'),
    (N'Dép Quai Ngang',       CAST(159000 AS DECIMAL(18,2)), N'Dép quai ngang unisex.',                     N'images/product/dep-quai-ngang.png',   N'Giày dép'),
    (N'Giày Thể Thao',        CAST(599000 AS DECIMAL(18,2)), N'Giày thể thao unisex.',                      N'images/product/giay-the-thao.png',    N'Giày dép'),
    (N'Giày Lười Nam',        CAST(519000 AS DECIMAL(18,2)), N'Giày lười nam lịch sự.',                     N'images/product/giay-luoi-nam.png',    N'Giày dép')
) AS sample(product_name, price, description, image, category_name)
INNER JOIN dbo.Categories AS category
    ON category.category_name = sample.category_name
WHERE NOT EXISTS
(
    SELECT 1
    FROM dbo.Products AS currentProduct
    WHERE currentProduct.product_name = sample.product_name
      AND currentProduct.category_id = category.category_id
);
GO

/* ============================================================================
   5. KIỂM TRA KẾT QUẢ VÀ CẢNH BÁO DỮ LIỆU CŨ KHÔNG TƯƠNG THÍCH
   ============================================================================ */

PRINT N'Hoàn tất hợp nhất vào BT08_SPRING_BOOT.';
PRINT N'Admin mặc định sẽ được DataInitializer của Spring Boot tạo khi chạy app.';
PRINT N'User cũ không dùng BCrypt bị khóa; dùng chức năng Quên mật khẩu để đặt lại.';
GO

SELECT
    category_id,
    category_name,
    icon,
    status
FROM dbo.Categories
ORDER BY category_id;
GO

SELECT
    product.product_id,
    product.product_name,
    product.price,
    product.image,
    category.category_id,
    category.category_name,
    product.created_date
FROM dbo.Products AS product
INNER JOIN dbo.Categories AS category
    ON category.category_id = product.category_id
ORDER BY product.product_id;
GO

SELECT
    id,
    username,
    fullname,
    email,
    phone,
    images,
    role,
    enabled,
    created_date,
    CASE
        WHEN password LIKE '$2a$%'
          OR password LIKE '$2b$%'
          OR password LIKE '$2y$%'
            THEN N'BCrypt - tương thích Spring Security'
        ELSE N'Không phải BCrypt - cần đặt lại mật khẩu'
    END AS password_compatibility
FROM dbo.Users
ORDER BY id;
GO

/* Các Category cũ bị bỏ qua vì tên dài hơn giới hạn entity mới. */
IF DB_ID(N'BT02_CRUD_JPA') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Category', N'U') IS NOT NULL
BEGIN
    EXEC sys.sp_executesql N'
        SELECT cate_id, cate_name, N''Tên dài quá 100 ký tự'' AS migration_error
        FROM BT02_CRUD_JPA.dbo.Category
        WHERE LEN(LTRIM(RTRIM(cate_name))) > 100;
    ';
END;
GO

/* Các User cũ bị bỏ qua do vượt giới hạn hoặc trùng username/email. */
IF DB_ID(N'BT02_CRUD_JPA') IS NOT NULL
   AND OBJECT_ID(N'BT02_CRUD_JPA.dbo.Users', N'U') IS NOT NULL
BEGIN
    EXEC sys.sp_executesql N'
        SELECT
            oldUser.id,
            oldUser.username,
            oldUser.email,
            N''Bị bỏ qua: trùng dữ liệu, tài khoản admin cũ hoặc vượt giới hạn cột mới''
                AS migration_warning
        FROM BT02_CRUD_JPA.dbo.Users AS oldUser
        WHERE LEN(LTRIM(RTRIM(oldUser.username))) > 50
           OR LEN(LTRIM(RTRIM(oldUser.email))) > 100
           OR LEN(LTRIM(RTRIM(oldUser.fullname))) > 100
           OR LEN(oldUser.password) > 255
           OR LOWER(LTRIM(RTRIM(oldUser.username))) = ''admin''
           OR LOWER(LTRIM(RTRIM(oldUser.email))) = ''admin@example.com''
           OR EXISTS
              (
                  SELECT 1 FROM dbo.Users AS currentUser
                  WHERE (
                         LOWER(currentUser.username) = LOWER(LTRIM(RTRIM(oldUser.username)))
                     AND LOWER(currentUser.email) <> LOWER(LTRIM(RTRIM(oldUser.email)))
                  )
                     OR (
                         LOWER(currentUser.email) = LOWER(LTRIM(RTRIM(oldUser.email)))
                     AND LOWER(currentUser.username) <> LOWER(LTRIM(RTRIM(oldUser.username)))
                  )
              );
    ';
END;
GO
