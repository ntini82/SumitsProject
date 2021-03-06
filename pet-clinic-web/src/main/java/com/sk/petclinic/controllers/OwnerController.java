package com.sk.petclinic.controllers;

import com.sk.petclinic.model.Owner;
import com.sk.petclinic.services.OwnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class OwnerController {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM="owners/createOrUpdateOwnerForm";
    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

//    @RequestMapping({"/owners","/owners/index","/owners/index.html"})
//    public String listOwners(Model model)
//    {
//        model.addAttribute("owners",ownerService.findAll());
//        return "owners/index";
//    }


    @RequestMapping({"/owners/find","/oups"})
    public String findOwners(Model model){
        model.addAttribute("owner", Owner.builder().build());
        return "owners/findOwners";
    }

    @GetMapping("/owners")
    public String processFindForm(Owner owner, BindingResult result,Model model){
        if(owner.getLastName() == null){
            owner.setLastName("");
        }
        List<Owner> results = ownerService.findAllByLastNameLike("%"+owner.getLastName()+"%");
        if (results.isEmpty()){
            result.rejectValue("lastName","notFound","not Found");
            return "owners/findOwners";
        }else if (results.size() == 1){
            owner = results.get(0);
            return "redirect:/owners/" + owner.getId();
        }else {
            model.addAttribute("selections",results);
            return "owners/ownersList";
        }

    }

    @GetMapping("/owners/{ownerId}")
    public ModelAndView showOwner(@PathVariable Long ownerId){
        ModelAndView mav = new ModelAndView("owners/ownerDetails");
        mav.addObject(ownerService.findById(ownerId));
        return mav;
    }

    @GetMapping("owners/new")
    public String initCreationForm(Model model){
        Owner owner = Owner.builder().build();
        model.addAttribute("owner",owner);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/owners/new")
    private String processCreationForm(@Valid Owner owner, BindingResult result){
        if(result.hasErrors()){
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        }else {
            Owner savedowner = ownerService.save(owner);
            return "redirect:/owners/"+savedowner.getId();
        }
    }

    @GetMapping("/owners/{ownerId}/edit")
    public String initUpdateOwnerForm(@PathVariable("ownerId") Long ownerId,Model model){
        model.addAttribute(ownerService.findById(ownerId));
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("owners/{ownerId}/edit")
    public String processUpdateOwnerForm(@Valid Owner owner,BindingResult result,@PathVariable("ownerId") Long ownerId){
        if (result.hasErrors()){
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        }else {
            owner.setId(ownerId);
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }
}
