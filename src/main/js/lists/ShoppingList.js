export default class ShoppingList {

    constructor({links = [], entityId, name, owners = [], items = [], lastModified = Date.now()} = {}) {

        this.key = 'entityId';
        this.links = links;
        this.entityId = entityId;
        this.name = name;
        this.owners = owners;
        this.items = items;
        this.lastModified = lastModified;
    }
}
