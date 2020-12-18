package com.example.ht7.controllers;

import com.example.ht7.entities.*;
import com.example.ht7.services.CartService;
import com.example.ht7.services.SoldItemsService;
import com.example.ht7.services.UserService;
import com.example.ht7.services.ItemService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HttpSession session;

    @Autowired
    private CartService cartService;

    @Autowired
    private SoldItemsService soldItemsService;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Value("${file.avatar.defaultItemPicture}")
    private String defaultItemPicture;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${color.primaris}")
    private String primColor;

    @Value("${color.secondary}")
    private String secondary;

    @GetMapping(value = "/")
    public String index(Model model) {
        ArrayList<ShopItems> items = (ArrayList<ShopItems>) itemService.getAllItems();
        ArrayList<ShopItems> items1 = new ArrayList<>();
        ArrayList<ShopItems> items2 = new ArrayList<>();
        for (ShopItems shopItem : items) {
            if (shopItem.isInTopPage()) {
                items1.add(shopItem);
            } else {
                items2.add(shopItem);
            }
        }

        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);
        List<Countries> countryList = itemService.getAllCountries();

        model.addAttribute("countries", countryList);
        model.addAttribute("IsTopItems", items1);
        model.addAttribute("items", items2);
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        model.addAttribute("currentUser", getUserData());
        return "index";
    }

    @PostMapping(value = "/addItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItem(@RequestParam(name = "item_name", defaultValue = "No Name") String name,
                          @RequestParam(name = "item_description", defaultValue = "No Description") String description,
                          @RequestParam(name = "item_price", defaultValue = "0") double price,
                          @RequestParam(name = "item_star", defaultValue = "0") int star,
                          @RequestParam(name = "item_smallPic", defaultValue = "https://png.pngtree.com/element_our/png/20181205/question-mark-vector-icon-png_256683.jpg") String sm_picture,
                          @RequestParam(name = "item_largePic", defaultValue = "https://png.pngtree.com/element_our/png/20181205/question-mark-vector-icon-png_256683.jpg") String lg_picture,
                          @RequestParam(name = "added_date", defaultValue = "1991-02-02") Date added_date,
                          @RequestParam(name = "brand_id", defaultValue = "") Long brandId,
                          @RequestParam(name = "top_page", defaultValue = "0") boolean top,
                          @RequestParam(name = "category_id", defaultValue = "0") Long categoryId
    ) {
        Brands brn = itemService.getBrand(brandId);
        ShopItems item = new ShopItems();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setStars(star);
        item.setSmallPicURL(sm_picture);
        item.setLargePicURL(lg_picture);
        item.setAddedDate(added_date);
        item.setBrands(brn);
        item.setQuantity(10);
        item.setInTopPage(top);
        itemService.saveItem(item);

        Categories cat = itemService.getCategory(categoryId);
        if (cat != null) {
            ShopItems items = itemService.getItem(item.getId());
            System.out.println(items);
            if (items != null) {
                List<Categories> categories = items.getCategories();
                if (categories == null) {
                    categories = new ArrayList<>();
                }
                categories.add(cat);

                items.setCategories(categories);
                itemService.saveItem(items);

                return "redirect:/admin";
            }
        }

        return "redirect:/";
    }

    @GetMapping(value = "/view/{idshka}")
    public String readMore(Model model,
                           @PathVariable(name = "idshka") Long id) {
        ShopItems items = itemService.getItem(id);
        model.addAttribute("item", items);

        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("categories", categories);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);

        List<Picture> pictures = itemService.getAllPictures(items);
        model.addAttribute("pictures", pictures);

        List<Comment> comments = itemService.getAllCommentsByItem(items);
        model.addAttribute("comments", comments);

        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        model.addAttribute("currentUser", getUserData());


        return "view";
    }

    @GetMapping(value = "/details/{idshka}")
    public String details(Model model,
                          @PathVariable(name = "idshka") Long id) {
        ShopItems item = itemService.getItem(id);
        model.addAttribute("item", item);
        List<Countries> countryList = itemService.getAllCountries();
        List<Brands> brandsList = itemService.getAllBrands();
        List<Categories> categoriesList = item.getCategories();
        List<Picture> pictures = itemService.getAllPictures(item);

        List<Categories> categoriesList2 = itemService.getAllCategories();
        List<Categories> categoriesList3 = itemService.getAllCategories();
        for (int i = 0; i < categoriesList.size(); i++) {
            Categories category = itemService.getCategory(categoriesList.get(i).getId());
            if (categoriesList2.contains(category)) {
                categoriesList3.remove(category);
            }
        }
        model.addAttribute("countries", countryList);
        model.addAttribute("categories", categoriesList);
        model.addAttribute("categoriesWithout", categoriesList3);
        model.addAttribute("brands", brandsList);
        model.addAttribute("pictures", pictures);

        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        model.addAttribute("currentUser", getUserData());
        return "details";
    }


    @GetMapping(value = "/editItem/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editItem(Model model,
                           @PathVariable(name = "idshka") Long id) {
        ShopItems item = itemService.getItem(id);
        model.addAttribute("item", item);
        List<Countries> countryList = itemService.getAllCountries();
        List<Brands> brandsList = itemService.getAllBrands();
        List<Categories> categoriesList = item.getCategories();

        List<Categories> categoriesList2 = itemService.getAllCategories();
        List<Categories> categoriesList3 = itemService.getAllCategories();
        for (int i = 0; i < categoriesList.size(); i++) {
            Categories category = itemService.getCategory(categoriesList.get(i).getId());
            if (categoriesList2.contains(category)) {
                categoriesList3.remove(category);
            }
        }
        model.addAttribute("countries", countryList);
        model.addAttribute("categories", categoriesList);
        model.addAttribute("categoriesWithout", categoriesList3);

        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        model.addAttribute("brands", brandsList);
        return "editItem";
    }

    @PostMapping(value = "/saveItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveItem(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "item_name", defaultValue = "No Name") String name,
                           @RequestParam(name = "item_description", defaultValue = "No Description") String description,
                           @RequestParam(name = "item_price", defaultValue = "0") double price,
                           @RequestParam(name = "item_star", defaultValue = "0") int star,
                           @RequestParam(name = "item_smallPic", defaultValue = "https://png.pngtree.com/element_our/png/20181205/question-mark-vector-icon-png_256683.jpg") String sm_picture,
                           @RequestParam(name = "item_largePic", defaultValue = "https://png.pngtree.com/element_our/png/20181205/question-mark-vector-icon-png_256683.jpg") String lg_picture,
                           @RequestParam(name = "country_id", defaultValue = "") Long countryId,
                           @RequestParam(name = "brand_id", defaultValue = "") Long brandId,
                           @RequestParam(name = "top_page", defaultValue = "0") boolean top
    ) {

        ShopItems item = itemService.getItem(id);

        if (item != null) {
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(star);
            item.setSmallPicURL(sm_picture);
            item.setLargePicURL(lg_picture);
            item.setBrands(itemService.getBrand(brandId));
            item.setInTopPage(top);
        }
        itemService.saveItem(item);
        return "redirect:/editItem/" + id;
    }

    @PostMapping(value = "/deleteItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteItem(@RequestParam(name = "id") Long id) {
        ShopItems item = itemService.getItem(id);
        if (item != null) {
            itemService.deleteItem(item);
        }
        return "redirect:/";
    }

    @GetMapping(value = "/allItems")
    public String allItems(Model model) {
        List<ShopItems> shopItems = itemService.getAllItems();
        model.addAttribute("items", shopItems);

        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "index";
    }

    @GetMapping(value = "/searchItem")
    public String searchItem(Model model,
                             @RequestParam(name = "search") String name,
                             @RequestParam(name = "brand_id", defaultValue = "0") Long brand_id,
                             @RequestParam(name = "priceFrom", defaultValue = "0") String priceFrom,
                             @RequestParam(name = "priceTo", defaultValue = "0") String priceTo,
                             @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        List<ShopItems> items2 = itemService.getItemsByNamePriceAsc(name);
        Brands brand = itemService.getBrand(brand_id);

        List<ShopItems> items = itemService.getItemsByNamePriceDesc(name);

        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("items", items);
        model.addAttribute("name", name);
        model.addAttribute("order", order);
        model.addAttribute("price_from", priceFrom);
        model.addAttribute("price_to", priceTo);
        model.addAttribute("oneBrand", brand);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);

        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "search";

    }

    @GetMapping(value = "/search")
    public String searchItem(Model model,
                             @RequestParam(name = "search", defaultValue = "") String name,
                             @RequestParam(name = "brand_id", defaultValue = "0") Long brand_id,
                             @RequestParam(name = "brand_name", defaultValue = "") String brandName,
                             @RequestParam(name = "priceFrom", defaultValue = "0") String priceFrom,
                             @RequestParam(name = "priceTo", defaultValue = "9999999") String priceTo,
                             @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        System.out.println(brandName);
        Brands brand = itemService.getBrand(brand_id);
        List<ShopItems> items2 = itemService.getItemsByNamePriceAsc(name);
        if (!brandName.equals("")) {
            List<Brands> brandsList = itemService.getAllBrands();
            for (Brands brands : brandsList) {
                if (brands.getName().equals(brandName)) {
                    brand = itemService.getBrand(brands.getId());
                    brand_id = brand.getId();
                    System.out.println(brand_id);
                }
            }
        }

        List<ShopItems> items;

        if (priceFrom.equals("0") && priceTo.equals("9999999")) {
            if (order.equals("asc")) {
                if (brand_id == 0) {
                    items = itemService.getItemsByNamePriceAsc(name);
                } else {
                    items = itemService.getItemsByBrandAndByNamePriceAsc(brand, name);
                }
            } else {
                if (brand_id == 0) {
                    items = itemService.getItemsByNamePriceDesc(name);
                } else {
                    items = itemService.getItemsByBrandAndByNamePriceDesc(brand, name);
                }
            }
        } else {
            if (order.equals("asc")) {
                items = itemService.getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceAsc(brand, name, Double.parseDouble(priceFrom), Double.parseDouble(priceTo));
            } else {
                items = itemService.getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceDesc(brand, name, Double.parseDouble(priceFrom), Double.parseDouble(priceTo));
            }
        }


        model.addAttribute("items", items);
        model.addAttribute("name", name);
        model.addAttribute("order", order);
        model.addAttribute("price_from", priceFrom);
        model.addAttribute("price_to", priceTo);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("oneBrand", brand);
        model.addAttribute("brands", brandsList);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        return "search";
    }


    @GetMapping(value = "/filteringAsc")
    public String filteringAsc(Model model,
                               @RequestParam(name = "names") String name,
                               @RequestParam(name = "price_from", defaultValue = "-2") double price_from,
                               @RequestParam(name = "price_to", defaultValue = "-1") double price_to) {
        if (price_from == (-2) && price_to == (-1)) {
            List<ShopItems> items1 = itemService.getItemsByNamePriceAsc(name);
            if (items1 != null) {
                model.addAttribute("name", name);
                model.addAttribute("items", items1);
                model.addAttribute("currentUser", getUserData());
                model.addAttribute("primaris", primColor);
                model.addAttribute("secondary", secondary);
                return "search";
            }
        } else {
            List<ShopItems> items2 = itemService.getItemsByNameAndPriceBetweenOrderByPriceAsc(name, price_from, price_to);
            if (items2 != null) {
                model.addAttribute("name", name);
                model.addAttribute("items", items2);
                return "search";
            }
        }
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "search";
    }

    @GetMapping(value = "/filteringDesc")
    public String filteringDesc(Model model,
                                @RequestParam(name = "names") String name,
                                @RequestParam(name = "price_from", defaultValue = "-2") double price_from,
                                @RequestParam(name = "price_to", defaultValue = "-1") double price_to) {
        if (price_from == (-2) && price_to == (-1)) {
            List<ShopItems> items1 = itemService.getItemsByNamePriceDesc(name);
            if (items1 != null) {
                model.addAttribute("name", name);
                model.addAttribute("items", items1);
                return "search";
            }
        } else {
            List<ShopItems> items2 = itemService.getItemsByNameAndPriceBetweenOrderByPriceDesc(name, price_from, price_to);
            if (items2 != null) {
                model.addAttribute("name", name);
                model.addAttribute("items", items2);
                return "search";
            }
        }
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "search";
    }

    @GetMapping(value = "/editBrands")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editBrands(Model model) {
        List<Brands> brandsList = itemService.getAllBrands();
        List<Countries> countriesList = itemService.getAllCountries();

        model.addAttribute("brands", brandsList);
        model.addAttribute("countries", countriesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        return "editBr";
    }

    @PostMapping(value = "/addBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addBrand(@RequestParam(name = "brand_name", defaultValue = "") String brand_name,
                           @RequestParam(name = "country_id", defaultValue = "0") Long country_id) {
        boolean check = true;
        List<Brands> brands = itemService.getAllBrands();
        for (Brands brands1 : brands) {
            if (brands1.getName().toLowerCase().equals(brand_name.toLowerCase())) {
                check = false;
            }
        }
        Countries country = itemService.getCountry(country_id);
        if (check) {
            if (country != null) {
                itemService.addBrand(new Brands(null, brand_name, country));
            }
        }

        return "redirect:/brands";
    }

    @PostMapping(value = "/addCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addBrand(@RequestParam(name = "country_name", defaultValue = "") String country_name,
                           @RequestParam(name = "country_code", defaultValue = "") String code) {
        boolean check = true;
        List<Countries> countriesList = itemService.getAllCountries();
        for (Countries countries : countriesList) {
            if (countries.getName().toLowerCase().equals(country_name.toLowerCase())) {
                check = false;
            }
        }
        if (check) {
            itemService.addCountry(new Countries(null, country_name, code));
        }

        return "redirect:/countries";
    }

    @PostMapping(value = "/saveCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveCountry(@RequestParam(name = "country_name", defaultValue = "") String country_name,
                              @RequestParam(name = "id", defaultValue = "0") Long id,
                              @RequestParam(name = "country_code", defaultValue = "") String code) {
        boolean check = true;
        Countries country = itemService.getCountry(id);
        List<Countries> countriesList = itemService.getAllCountries();
        for (Countries countries : countriesList) {
            if (countries.getName().toLowerCase().equals(country_name.toLowerCase())) {
                if (country.getName().equals(country_name)) {
                    continue;
                } else {
                    check = false;
                }
            }
        }
        if (check) {
            country.setName(country_name);
            country.setCode(code);
            itemService.saveCountry(country);
        }
        return "redirect:/countries";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String indexAdmin(Model model) {
        ArrayList<ShopItems> items = (ArrayList<ShopItems>) itemService.getAllItems();

        List<Brands> brandsList = itemService.getAllBrands();
        List<Countries> countriesList = itemService.getAllCountries();
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("brands", brandsList);
        model.addAttribute("categories", categoriesList);
        model.addAttribute("countries", countriesList);
        model.addAttribute("items", items);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "indexAdmin";
    }

    @GetMapping(value = "/categories")
    public String getCategories(Model model) {
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "categoryAdmin";
    }

    @PostMapping(value = "/addCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addCategory(@RequestParam(name = "category_name", defaultValue = "No Name") String categoryName,
                              @RequestParam(name = "logoUrl", defaultValue = "No Logo") String logoUrl) {
        Categories categories = new Categories();
        categories.setName(categoryName);
        categories.setLogoURL(logoUrl);
        itemService.addCategory(categories);
        return "redirect:/categories";
    }

    @GetMapping(value = "/countries")
    public String getCountries(Model model) {
        List<Countries> countriesList = itemService.getAllCountries();
        model.addAttribute("countries", countriesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "countryAdmin";
    }

    @GetMapping(value = "/brands")
    public String getBrands(Model model) {
        List<Brands> brandsList = itemService.getAllBrands();
        List<Countries> countriesList = itemService.getAllCountries();

        model.addAttribute("brands", brandsList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        model.addAttribute("countries", countriesList);

        return "brandsAdmin";
    }

    @PostMapping(value = "/assigncategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignCategory(@RequestParam(name = "item_id") Long itemId,
                                 @RequestParam(name = "category_id") Long categoryId) {
        Categories cat = itemService.getCategory(categoryId);
        if (cat != null) {
            ShopItems item = itemService.getItem(itemId);
            if (item != null) {
                List<Categories> categories = item.getCategories();
                if (categories == null) {
                    categories = new ArrayList<>();
                }

                categories.add(cat);
                itemService.saveItem(item);

                return "redirect:/editItem/" + itemId;
            }
        }

        return "redirect:/admin";
    }

    @PostMapping(value = "/assigncategoryMinus")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignCategoryMinus(@RequestParam(name = "item_id") Long itemId,
                                      @RequestParam(name = "category_id") Long categoryId) {
        Categories cat = itemService.getCategory(categoryId);
        if (cat != null) {
            ShopItems item = itemService.getItem(itemId);
            if (item != null) {
                List<Categories> categories = item.getCategories();
                if (categories == null) {
                    categories = new ArrayList<>();
                }
                categories.remove(cat);

                itemService.saveItem(item);

                return "redirect:/editItem/" + itemId;
            }
        }

        return "redirect:/admin";
    }

    @GetMapping(value = "/editCountry/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsCountry(Model model, @PathVariable(name = "idshka") Long id) {
        Countries country = itemService.getCountry(id);
        String editName = "country";
        model.addAttribute("country", country);
        model.addAttribute("editName", editName);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "detailsCountry";
    }

    @GetMapping(value = "/editBrand/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsBrand(Model model, @PathVariable(name = "idshka") Long id) {
        Brands brand = itemService.getBrand(id);
        List<Countries> countriesList = itemService.getAllCountries();
        model.addAttribute("brand", brand);
        model.addAttribute("countries", countriesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        return "detailsBrands";
    }

    @PostMapping(value = "/saveBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveBrand(@RequestParam(name = "brand_name", defaultValue = "") String brand_name,
                            @RequestParam(name = "id", defaultValue = "0") Long brandId,
                            @RequestParam(name = "country_id", defaultValue = "0") Long countryId) {
        Brands brands = itemService.getBrand(brandId);
        List<Countries> countriesList = itemService.getAllCountries();
        if (brands != null) {
            Countries countries = itemService.getCountry(countryId);
            if (countries != null) {
                brands.setName(brand_name);
                brands.setCountry(countries);
                itemService.saveBrands(brands);
            }
        }
        return "redirect:/brands";
    }

    @GetMapping(value = "/editCategory/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCategory(Model model, @PathVariable(name = "idshka") Long id) {
        Categories categories = itemService.getCategory(id);
        if (categories != null) {
            model.addAttribute("category", categories);
            model.addAttribute("currentUser", getUserData());
            model.addAttribute("primaris", primColor);
            model.addAttribute("secondary", secondary);
        }
        return "detailsCategory";
    }

    @PostMapping(value = "/saveCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveCatefory(@RequestParam(name = "category_name", defaultValue = "No Name") String category_name,
                               @RequestParam(name = "id", defaultValue = "0") Long catergoryID,
                               @RequestParam(name = "logoUrl", defaultValue = "No URL") String logoUrl) {
        Categories categories = itemService.getCategory(catergoryID);
        if (categories != null) {
            categories.setName(category_name);
            categories.setLogoURL(logoUrl);
            itemService.saveCategory(categories);
        }

        return "redirect:/categories";
    }

    @GetMapping(value = "/viewAdmin/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String viewItem(Model model, @PathVariable(name = "idshka") Long id) {
        ShopItems items = itemService.getItem(id);

        model.addAttribute("item", items);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "viewItemAdmin";
    }

    @PostMapping(value = "/deleteCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteCountry(@RequestParam(name = "id", defaultValue = "0") Long id) {
        Countries countries = itemService.getCountry(id);
        if (countries != null) {
            itemService.deleteCountry(countries);
        }
        return "redirect:/countries";
    }

    @PostMapping(value = "/deleteCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteCategory(@RequestParam(name = "id", defaultValue = "0") Long id) {
        Categories category = itemService.getCategory(id);
        if (category != null) {
            itemService.deleteCategory(category);
        }
        return "redirect:/categories";
    }

    @PostMapping(value = "/deleteBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteBrand(@RequestParam(name = "id", defaultValue = "0") Long id) {
        Brands brand = itemService.getBrand(id);
        if (brand != null) {
            itemService.deleteBrand(brand);
        }
        return "redirect:/brands";
    }

    @PostMapping(value = "/by")
    public String byCateg(Model model, @RequestParam(name = "idCategoria", defaultValue = "-1") Long categoryID) {
        Categories category = itemService.getCategory(categoryID);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        if (category != null) {
            List<ShopItems> listCategory = itemService.getAllByCategories(category);
            model.addAttribute("byCategory", listCategory);
            model.addAttribute("category", category);
            return "byCategory";
        }
        return "redirect:/index";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        model.addAttribute("brands", brandsList);
        return "profile";
    }


    @GetMapping(value = "/addItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItem(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        List<Categories> categoriesList = itemService.getAllCategories();
        model.addAttribute("categories", categoriesList);
        List<Brands> brandsList = itemService.getAllBrands();
        List<Countries> countryList = itemService.getAllCountries();
        model.addAttribute("brands", brandsList);
        model.addAttribute("countries", countryList);
        return "addItem";
    }

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

    @GetMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "register";
    }

    @PostMapping(value = "/register")
    public String toRegister(@RequestParam(name = "user_email") String email,
                             @RequestParam(name = "user_password") String password,
                             @RequestParam(name = "re_user_password") String rePassword,
                             @RequestParam(name = "user_full_name") String fullName
    ) {
        if (password.equals(rePassword)) {
            Users newUser = new Users();
            newUser.setFullName(fullName);
            newUser.setPassword(password);
            newUser.setEmail(email);
            if (userService.createUser(newUser) != null) {
                return "redirect:/register?success";
            }
        }

        return "redirect:/register?error";
    }

    @GetMapping(value = "/manageUsers")
    public String manageUsers(Model model) {
        List<Users> usersList = userService.getAllUsers();
        List<Roles> rolesList = userService.getAllRoles();
        model.addAttribute("rolesList", rolesList);

        model.addAttribute("users", usersList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);

        return "manageUsers";
    }

    @PostMapping(value = "/admReg")
    public String admReg(@RequestParam(name = "user_email") String email,
                         @RequestParam(name = "user_password") String password,
                         @RequestParam(name = "re_user_password") String rePassword,
                         @RequestParam(name = "user_full_name") String fullName
    ) {
        if (password.equals(rePassword)) {
            Users newUser = new Users();
            newUser.setFullName(fullName);
            newUser.setPassword(password);
            newUser.setEmail(email);
            if (userService.createUser(newUser) != null) {
                return "redirect:/manageUsers?success";
            }
        }

        return "redirect:/manageUsers?error";
    }

    @GetMapping(value = "/editUsers/{idshka}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editUsers(Model model,
                            @PathVariable(name = "idshka") Long id) {
        Users user = userService.getUserById(id);
        model.addAttribute("user", user);
        List<Roles> userRoles = user.getRoles();
        List<Roles> rolesList1 = userService.getAllRoles();
        List<Roles> rolesList2 = userService.getAllRoles();

        for (int i = 0; i < userRoles.size(); i++) {
            Roles category = userService.getRole(userRoles.get(i).getId());
            if (rolesList1.contains(category)) {
                rolesList2.remove(category);
            }
        }

        model.addAttribute("userRoles", userRoles);
        model.addAttribute("userRolesWithout", rolesList2);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "editUsr";
    }

    @GetMapping(value = "/editUsersSimple/{idshka}")
    @PreAuthorize("isAuthenticated()")
    public String editUsersSimple(Model model,
                                  @PathVariable(name = "idshka") Long id) {
        Users user = userService.getUserById(id);
        if (user.getEmail().equals(getUserData().getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("currentUser", getUserData());
            model.addAttribute("primaris", primColor);
            model.addAttribute("secondary", secondary);
            return "editSimpleUsr";
        }
        return null;
    }

    @GetMapping(value = "/viewUsr/{idshka}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String viewUsr(Model model, @PathVariable(name = "idshka") Long id) {
        Users user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "viewUsrAdmin";
    }

    @GetMapping(value = "/viewUsrSimple/{idshka}")
    @PreAuthorize("isAuthenticated()")
    public String viewUsrSimple(Model model, @PathVariable(name = "idshka") Long id) {
        Users user = userService.getUserById(id);
        if (user.getEmail().equals(getUserData().getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("currentUser", getUserData());
            model.addAttribute("primaris", primColor);
            model.addAttribute("secondary", secondary);
            return "viewUsrSimple";
        }
        return null;
    }

    @PostMapping(value = "/saveUserSimple")
    @PreAuthorize("isAuthenticated()")
    public String saveUserSimple(
            @RequestParam(name = "user_password") String password,
            @RequestParam(name = "user_full_name") String fullName,
            @RequestParam(name = "id") Long id
    ) {

        Users user = userService.getUserById(id);

        if (user != null) {
            if (password != null && password.equals(null) && password.equals(" ") && password.equals("")) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
            }
            user.setFullName(fullName);
        }
        userService.saveUser(user);
        return "redirect:/profile";
    }

    @PostMapping(value = "/saveUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admReg(
            @RequestParam(name = "user_password") String password,
            @RequestParam(name = "user_full_name") String fullName,
            @RequestParam(name = "id") Long id
    ) {

        Users user = userService.getUserById(id);

        if (user != null) {
            if (password != null) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
            }
            user.setFullName(fullName);
        }
        userService.saveUser(user);
        return "redirect:/editUsers/" + id;
    }

    @PostMapping(value = "/deleteUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        Users user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/manageUsers";
    }

    @GetMapping(value = "/roles")
    public String getRoles(Model model) {
        List<Roles> rolesList = userService.getAllRoles();
        model.addAttribute("rolesList", rolesList);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "rolesAdmin";
    }

    @PostMapping(value = "/assignRoles")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignRoles(@RequestParam(name = "user_id") Long userId,
                              @RequestParam(name = "role_id") Long roleId) {
        Roles role = userService.getRole(roleId);
        if (role != null) {
            Users user = userService.getUserById(userId);
            if (user != null) {
                List<Roles> roles = user.getRoles();
                if (roles == null) {
                    roles = new ArrayList<>();
                }

                roles.add(role);
                userService.saveUser(user);

                return "redirect:/editUsers/" + userId;
            }
        }

        return "redirect:/admin";
    }

    @PostMapping(value = "/assignRolesMinus")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignRolesMinus(@RequestParam(name = "user_id") Long userId,
                                   @RequestParam(name = "role_id") Long roleId) {
        Roles role = userService.getRole(roleId);
        if (role != null) {
            Users user = userService.getUserById(userId);
            if (user != null) {
                List<Roles> roles = user.getRoles();
                if (roles == null) {
                    roles = new ArrayList<>();
                }
                roles.remove(role);

                userService.saveUser(user);

                return "redirect:/editUsers/" + userId;
            }
        }

        return "redirect:/admin";
    }

    @PostMapping(value = "/uploadavatar")
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(@RequestParam(name = "user_ava") MultipartFile file) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                Users currentUser = getUserData();

                String picName = DigestUtils.sha1Hex("yamete_" + currentUser.getId() + "_!kudasai!");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                currentUser.setUserAvatar(picName);

                userService.saveUser(currentUser);
                return "redirect:/profile?success";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/";
    }

    @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;

        if (url != null && !url.equals("null")) {
            pictureURL = viewPath + url + ".jpg";
        }

        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/viewimage/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody
    byte[] viewItemPhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultItemPicture;
        if (url != null) {
            pictureURL = viewPath + url + ".jpg";
        }

        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (IOException e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultItemPicture);
            in = resource.getInputStream();
        }

        return IOUtils.toByteArray(in);
    }

    @PostMapping("/addPhoto")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addPhoto(@RequestParam(name = "file") MultipartFile file,
                           @RequestParam(name = "item_id") Long id) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                ShopItems items = itemService.getItem(id);

                String picName = DigestUtils.sha1Hex("yamete_" + file.getName() + LocalDateTime.now() + "_?kudasai!");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                Picture picture = new Picture();
                picture.setUrl(picName);
                picture.setItem(items);
                itemService.addPicture(picture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/view/" + id;
    }

    @PostMapping("/removePhoto")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String removePhoto(@RequestParam(name = "photo_id") Long id,
                              @RequestParam(name = "item_id") Long item_id) {
        Picture picture = itemService.getPicture(id);
        itemService.deletePicture(picture);

        return "redirect:/view/" + item_id;
    }

    @PostMapping(value = "order")
    public String order(HttpServletResponse response,
                        @RequestParam(name = "order", defaultValue = "asc") String order) {
        Cookie cookie = new Cookie("order", order);
        try {
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    @GetMapping(value = "/basket")
    public String basket(Model model, HttpSession session) {
        if (session.getAttribute("basket") == null) {
            List<ShopItems> basket = new ArrayList<>();
            session.setAttribute("basket", basket);
            model.addAttribute("total", "0,00");
        } else {
            List<ShopItems> basket = (List<ShopItems>) session.getAttribute("basket");
            Double total = 0.0;
            for (ShopItems it : basket) {
                for (Integer i = 0; i < it.getQuantity(); i++) {
                    total = total + it.getPrice();
                }
            }
            model.addAttribute("total", total);
        }
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "basket";
    }

    @GetMapping(value = "/order/{id}")
    public String buy(@PathVariable("id") Long id) {
        if (session.getAttribute("basket") == null) {
            List<ShopItems> basket = new ArrayList<>();
            ShopItems item = itemService.getItem(id);
            item.setQuantity(1);
            basket.add(item);
            session.setAttribute("basket", basket);
        } else {
            List<ShopItems> basket = (List<ShopItems>) session.getAttribute("basket");
            int index = this.exists(id, basket);
            if (index == -1) {
                ShopItems item = itemService.getItem(id);
                item.setQuantity(1);
                basket.add(item);
            } else {
                Integer quantity = basket.get(index).getQuantity() + 1;
                basket.get(index).setQuantity(quantity);
            }
            session.setAttribute("basket", basket);
        }


        return "redirect:/basket";
    }

    private int exists(Long id, List<ShopItems> basket) {
        for (int i = 0; i < basket.size(); i++) {
            if (basket.get(i).getId() == id) {
                return i;
            }
        }

        return -1;
    }

    @RequestMapping(value = "/clearBasket", method = RequestMethod.GET)
    public String clearBasket() {
        List<ShopItems> basket = new ArrayList<>();
        session.setAttribute("basket", basket);
        return "redirect:/basket";
    }

    @RequestMapping(value = "/removeFromBasket/{id}", method = RequestMethod.GET)
    public String remove(@PathVariable("id") Long id) {
        List<ShopItems> basket = (List<ShopItems>) session.getAttribute("basket");
        int index = this.exists(id, basket);

        basket.remove(index);
        session.setAttribute("basket", basket);
        return "redirect:/basket";
    }

    @RequestMapping(value = "/minusItemFromBasket/{id}", method = RequestMethod.GET)
    public String decrease(@PathVariable("id") Long id) {
        List<ShopItems> basket = (List<ShopItems>) session.getAttribute("basket");
        int index = this.exists(id, basket);

        if (basket.get(index).getQuantity() != 1) {
            Integer quantity = basket.get(index).getQuantity();
            basket.get(index).setQuantity(quantity - 1);
        } else {
            basket.remove(index);
        }
        session.setAttribute("basket", basket);
        return "redirect:/basket";
    }

    @PostMapping(value = "/payment")
    public String payment(Model model) {
        List<ShopItems> basket = (List<ShopItems>) session.getAttribute("basket");
        Cart payment = new Cart();
        payment.setPaid_date(LocalDateTime.now());
        cartService.addCart(payment);

        Long index = Long.valueOf(cartService.getCarts().size());

        for (ShopItems it : basket) {
            SoldItems soldItem = new SoldItems();
            soldItem.setItem(it);
            soldItem.setQuantity(it.getQuantity());
            soldItem.setCart(index);
            soldItemsService.addItem(soldItem);
        }

        return "redirect:/clearBasket";
    }

    @GetMapping(value = "/basket/{id}")
    public String basket(Model model, @PathVariable("id") Long id) {
        List<SoldItems> basket = soldItemsService.getItemsByCart(id);
        List<ShopItems> items = new ArrayList<>();

        for (SoldItems s : basket) {
            ShopItems item = new ShopItems();
            item = s.getItem();
            item.setQuantity(s.getQuantity());
            items.add(item);
        }

        model.addAttribute("basket", items);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("primaris", primColor);
        model.addAttribute("secondary", secondary);
        return "sold";
    }

    @PostMapping(value = "/addComment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "comment_text") String text,
                             @RequestParam(name = "id") Long id) {
        System.out.println("Enter the Method");
        Users currentUser = getUserData();
        System.out.println(currentUser);
        ShopItems items = itemService.getItem(id);

        Comment comment = new Comment();
        comment.setAddedDate(new Date(System.currentTimeMillis()));
        comment.setAuthor(currentUser);
        comment.setComment(text);
        comment.setItems(items);
        itemService.saveComment(comment);

        return "redirect:/view/"+id;
    }

    @PostMapping(value = "/saveComment")
    @PreAuthorize("isAuthenticated()")
    public String saveComment(@RequestParam(name = "comment_text") String text,
                             @RequestParam(name = "id") Long id,
                              @RequestParam(name = "author_id") Long authId,
                              @RequestParam(name = "item_id") Long item_id) {
        Users currentUser = getUserData();
        if (currentUser.getId().equals(authId)) {
            ShopItems item = itemService.getItem(id);
            Comment comment = itemService.getComment(id);

            if (item != null && comment != null && !comment.equals(" ")) {
                comment.setComment(text);
            }

            itemService.saveComment(comment);
        }


        return "redirect:/view/"+item_id;
    }

    @PostMapping("/removeComment")
    @PreAuthorize("isAuthenticated()")
    public String removeComment(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "item_id") Long item_id) {
        Comment comment = itemService.getComment(id);
        itemService.deleteComment(comment);

        return "redirect:/view/" + item_id;
    }


}

