package be.cytomine.image.server

import be.cytomine.image.AbstractImage

/**
 * TODOSTEVBEN: doc
 */
class StorageAbstractImage {
    Storage storage
    AbstractImage abstractImage

    static mapping = {
        version false
    }

    static StorageAbstractImage link(Storage storage, AbstractImage abstractImage) {
        def storageAbstractImage = StorageAbstractImage.findByStorageAndAbstractImage(storage, abstractImage)
        if (!storageAbstractImage) {
            storageAbstractImage = new StorageAbstractImage()
            storage.addToStorageAbstractImages(storageAbstractImage)
            abstractImage.addToStorageAbstractImages(storageAbstractImage)
            storage.refresh()
            abstractImage.refresh()
            storageAbstractImage.save(flush: true)
        }
    }

    static StorageAbstractImage unlink(Storage storage, AbstractImage abstractImage) {
        def storageAbstractImage = StorageAbstractImage.findByStorageAndAbstractImage(storage, abstractImage)
        if (storageAbstractImage) {
            storage.removeFromStorageAbstractImages(storageAbstractImage)
            abstractImage.removeFromStorageAbstractImages(storageAbstractImage)
            storage.refresh()
            abstractImage.refresh()
            storageAbstractImage.delete(flush: true)
        }
    }
}
