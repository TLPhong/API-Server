package logging
/*
There are Gallery
    IS: Collection of Resource,
    CAN:
        listing resource
        add resource
        remove resource
    PROPS: number of resource

There are Resource
    TYPE:
        Serial
            IS: collection of item
            PROPS: name,index
            CAN: be serving
        Single
            IS: one item inside or it is the resource
            PROPS: name
            CAN: be serving
     PROPS: name, number of item, tags, created time (epoch), deleted time(epoch).
 */
interface UsageLogFacade {

}
