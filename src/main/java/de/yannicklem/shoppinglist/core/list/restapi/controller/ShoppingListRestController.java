package de.yannicklem.shoppinglist.core.list.restapi.controller;

import de.yannicklem.restutils.controller.RestEntityController;

import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.list.restapi.service.ShoppingListRequestHandler;
import de.yannicklem.shoppinglist.core.list.restapi.service.ShoppingListResourceProcessor;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@RestController
@ExposesResourceFor(ShoppingList.class)
@RequestMapping(
    value = ShoppingListEndpoints.SHOPPING_LISTS_ENDPOINT, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE
    }
)
public class ShoppingListRestController extends RestEntityController<ShoppingList, String> {

    private static Logger LOGGER = getLogger(lookup().lookupClass());

    @Autowired
    public ShoppingListRestController(SLUserService slUserService, ShoppingListService shoppingListService,
        ShoppingListRequestHandler requestHandler, ShoppingListResourceProcessor resourceProcessor,
        EntityLinks entityLinks) {

        super(slUserService, shoppingListService, requestHandler, resourceProcessor, entityLinks);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(Principal principal) {

        HttpEntity<? extends Resources<? extends ShoppingList>> allEntities = this.getAllEntities(principal);
        Collection<? extends ShoppingList> content = allEntities.getBody().getContent();
        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElseThrow(() ->
                    new NotFoundException("User not found"));

        for (ShoppingList shoppingList : content) {
            requestHandler.handleBeforeDelete(shoppingList, currentUser);
            entityService.delete(shoppingList);
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/projections/{projectionName}")
    public HttpEntity<Resources<? extends ShoppingList>> getProjection(Principal principal,
        @PathVariable("projectionName") String projectionName) {

        HttpEntity<? extends Resources<? extends ShoppingList>> allEntities = getAllEntities(principal);
        Collection<? extends ShoppingList> allLists = allEntities.getBody().getContent();

        if (projectionName.equals("name_only")) {
            SLUser currentUser = principal == null ? null
                                                   : slUserService.findById(principal.getName()).orElseThrow(() ->
                        new NotFoundException("User not found"));
            List<ShoppingList> projections = new ArrayList<>();

            for (ShoppingList shoppingList : allLists) {
                projections.add(((ShoppingListResourceProcessor) resourceProcessor).process(shoppingList, currentUser,
                        projectionName));
            }

            return new HttpEntity<>(new Resources<>(projections));
        }

        return null;
    }
}
