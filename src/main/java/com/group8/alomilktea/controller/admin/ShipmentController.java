package com.group8.alomilktea.controller.admin;

import com.group8.alomilktea.entity.ShipmentCompany;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.IShipmentCompany;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/shipment")
public class ShipmentController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IShipmentCompany shipmentCompanyService;

    // List all ShipmentCompanies with pagination
    @RequestMapping("/list")
    public String showShipmentCompanyList(ModelMap model,
                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<ShipmentCompany> shipmentCompanyPage = shipmentCompanyService.getAll(PageRequest.of(page, size));

        model.addAttribute("shipments", shipmentCompanyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shipmentCompanyPage.getTotalPages());
        model.addAttribute("totalItems", shipmentCompanyPage.getTotalElements());

        model.addAttribute("pageSize", size);

        return "admin/shipments/apps-ecommerce-shipment-list";
    }

    // Show form to add a new ShipmentCompany
    @GetMapping("/add")
    public String addShipmentCompany(ModelMap model) {
        model.addAttribute("shipments", new ShipmentCompany());
        model.addAttribute("isEdit", false);
        model.addAttribute("shippers", userService.findAllShippers()); // Lấy danh sách shipper

        return "admin/shipments/apps-ecommerce-shipment-create";
    }

    // Save a new ShipmentCompany
    @PostMapping("/add/save")
    public String saveShipmentCompany(
            @RequestParam("methodname") String methodname,
            @RequestParam("price") Double price,
            @RequestParam("shipperId") Integer user_id,
            ModelMap model) {

        User user = userService.findById(user_id);

        ShipmentCompany shipmentCompany = new ShipmentCompany();
        shipmentCompany.setShipCname(methodname);
        shipmentCompany.setPrice(price);
        shipmentCompany.setUser(user);

        shipmentCompanyService.save(shipmentCompany);
        model.addAttribute("message", "Shipment Company created successfully!");
        return "redirect:/admin/shipment/list";
    }

    // Show form to edit an existing ShipmentCompany
    @GetMapping("/edit/{shipCid}")
    public String editShipmentCompany(@PathVariable Integer shipCid, ModelMap model) {
        ShipmentCompany shipmentCompany = shipmentCompanyService.findById(shipCid);
        if (shipmentCompany == null) {
            model.addAttribute("errorMessage", "Shipment Company not found!");
            return "redirect:/admin/shipment/list";
        }
        model.addAttribute("shipments", shipmentCompany);
        model.addAttribute("isEdit", true);
        model.addAttribute("shippers", userService.findAllShippers()); // Lấy danh sách shipper
        return "admin/shipments/apps-ecommerce-shipment-create";
    }

    // Save changes to an existing ShipmentCompany
    @PostMapping("/edit/save")
    public String updateShipmentCompany(
            @RequestParam("id") Integer id,
            @RequestParam("methodname") String methodname,
            @RequestParam("price") Double price,
            @RequestParam("shipperId") Integer user_id,
            ModelMap model) {

        ShipmentCompany shipmentCompany = shipmentCompanyService.findById(id);
        if (shipmentCompany == null) {
            model.addAttribute("errorMessage", "Shipment Company not found!");
            return "redirect:/admin/shipment/list";
        }

        User user = userService.findById(user_id);

        shipmentCompany.setShipCname(methodname);
        shipmentCompany.setPrice(price);
        shipmentCompany.setUser(user);

        shipmentCompanyService.save(shipmentCompany);
        model.addAttribute("message", "Shipment Company updated successfully!");
        return "redirect:/admin/shipment/list";
    }

    // Delete a ShipmentCompany
    @PostMapping("/delete/{shipCid}")
    public String deleteShipmentCompany(@PathVariable("shipCid") Integer shipCid, ModelMap model) {
        ShipmentCompany shipmentCompany = shipmentCompanyService.findById(shipCid);

        if (shipmentCompany == null) {
            model.addAttribute("errorMessage", "Shipment Company not found!");
            return "redirect:/admin/shipment/list";
        } else {
            shipmentCompanyService.deleteById(shipCid);
        }

        model.addAttribute("message", "Shipment Company deleted successfully!");
        return "redirect:/admin/shipment/list";
    }
}
