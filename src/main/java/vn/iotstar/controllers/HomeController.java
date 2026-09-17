package vn.iotstar.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;

@Controller
@RequiredArgsConstructor
// Bộ điều khiển cho trang chủ và các trang liên quan đến sản phẩm và danh mục
public class HomeController {
    private final IProductService productService;
    private final ICategoryService categoryService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("products", productService.findTop10Newest());
        model.addAttribute("categories", categoryService.findActive());
        return "web/home";
    }

    @GetMapping("/product")
    public String products(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 6,
                Sort.by("createdDate").descending());
        model.addAttribute("result", productService.findAll(keyword, pageable));
        model.addAttribute("keyword", keyword);
        return "web/product-list";
    }

    @GetMapping("/product/detail")
    public String detail(@RequestParam Long id, Model model) {
        model.addAttribute("product", productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "web/product-detail";
    }

    @GetMapping("/category")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findActive());
        return "web/category-list";
    }
}