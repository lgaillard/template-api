package fr.polytechnice.templateapi.resources;

import fr.polytechnice.templateapi.entities.Item;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemsResource {

    // In memory db for simplicity. Don't do this :)
    private static final List<Item> itemsDb;
    static {
        itemsDb = new ArrayList<>(Arrays.asList(
            new Item("Bryonia", "The stems of a plant growing on wastelands.", 2100, ZonedDateTime.now()),
            new Item("Mandrake root", "The root of a poisonous plant with magical properties.", 1499, ZonedDateTime.now()),
            new Item("Optima mater", "A catalyst highly valued by alchemists.", 6998, ZonedDateTime.now()),
            new Item("Sewant mushroom", "Large grey mushrooms growing in caves.", 900, ZonedDateTime.now())
        ));
    }


    public ItemsResource() {}


    @GET
    public List<Item> list() {
        return new ArrayList<>(itemsDb);
    }

    @GET
    @Path("/{name}")
    public Item get(@PathParam("name") String name) {
        for (Item item : itemsDb) {
            if(StringUtils.equals(item.name, name)) return item;
        }
        throw new NotFoundException("item not found");
    }

    @POST
    public Response create(Item item) {
        if(StringUtils.isEmpty(item.name)) throw new BadRequestException("invalid name");
        if(item.price < 0) throw new BadRequestException("invalid price");
        for (Item it : itemsDb) {
            if(StringUtils.equals(it.name, item.name)) throw new ConflictException("item already exists");
        }
        item.creationDate = ZonedDateTime.now();
        itemsDb.add(item);
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{name}")
    public Item update(@PathParam("name") String name, Item item) {
        for (Item it : itemsDb) {
            if(StringUtils.equals(it.name, name)) {
                if(item.price < 0) throw new BadRequestException("invalid price");
                it.description = item.description;
                it.price = item.price;
                return it;
            }
        }
        throw new NotFoundException("item not found");
    }
}
