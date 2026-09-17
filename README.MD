\-- CẤU HÌNH SQL VÀ MAIL GỬI --
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="MẬT KHẨU SQL"
$env:MAIL_USERNAME="EMAIL@gmail.com"
$env:MAIL_PASSWORD="MẬT KHẨU ỨNG DỤNG 16 SỐ"
$env:ADMIN_PASSWORD="MẬT KHẨU SQL"



-- CHẠY CHƯƠNG TRÌNH --

.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run



-- TRANG TRUY CẬP --

http://localhost:8081/login