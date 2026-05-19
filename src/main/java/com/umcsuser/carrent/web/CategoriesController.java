package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.services.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final VehicleCategoryConfigService categoryConfigService;

    public CategoriesController(VehicleCategoryConfigService categoryConfigService) {
        this.categoryConfigService = categoryConfigService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return categoryConfigService.findAllCategories();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return categoryConfigService.getByCategory(category);
    }
}

