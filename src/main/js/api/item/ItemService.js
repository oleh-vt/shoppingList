export default class ItemService {

    /*@ngInject*/
    constructor(articleService, itemRestService, listItemRestService) {

        this.articleService = articleService;

        this.restService = itemRestService;
        this.listItemRestService = listItemRestService;
        this.items = this.restService.container;

    }

    /**
     * Returns all items.
     *
     * @param {Boolean} refetch If true a request to the backend will be performed. If false the last fetched lists are
     *                  returned.
     * @returns {Array} All Items
     */
    getAllItems(refetch = false) {
        if (refetch || this.itemsAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.items;
    }

    getItemsOfList(list) {
        return this.listItemRestService.fetch({listId: list.entityId});
    }

    itemsAlreadyFetched() {
        return !this.items.fetching && !this.items.fetched;
    }

    addItemToList(item, list) {
        list.items.push(item);
        return this.articleService.createArticle(item.article)
            .then((createdArticle) => {
                item.article.entityId = createdArticle.entityId;
                return this.listItemRestService.create(item, {listId: list.entityId});
            });
    }

    updateItemOfList(item, list) {
        return this.listItemRestService.update(item, {listId: list.entityId});
    }

    deleteItemOfList(item, list) {
        return this.listItemRestService.delete(item, {listId: list.entityId});
    }
}