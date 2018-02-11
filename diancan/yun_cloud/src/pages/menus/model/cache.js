import { time } from '@/utils/date.js'

function getCache(shopInfo, shopActivityList, shopMenu, Vue) {
    let timeStamp = time()
    let currentShopId = shopInfo.model.fsShopGUID
    let currentShopActivityList = shopActivityList.model
    let currentShopMenu = shopMenu.model
    if (localStorage.cache) {
        let cache = JSON.parse(localStorage.cache)
        for (let item of cache) {
            if (item.shopId == currentShopId && item.timeStamp + 6 * 24 * 60 > timeStamp) {
                Vue.$store.commit('SAVE_MENUS_LIST', item.menusList)
                Vue.$store.commit('INIT_GOODS_INFO', item.goodInfo)
                Vue.$store.commit('SAVE_ACTIVITY_LIST', currentShopActivityList)
                sessionStorage.shopActivityList = JSON.stringify(currentShopActivityList) //传给定单确认页的所有活动信息
                return
            }
        }
    }
    Vue.$store.commit('SAVE_MENUS_LIST', currentShopMenu)
    Vue.$store.commit('SAVE_ACTIVITY_LIST', currentShopActivityList)
    sessionStorage.shopActivityList = JSON.stringify(currentShopActivityList) //传给定单确认页的所有活动信息
}


function saveCache(GOODS_INFO, MENUS_LIST, SHOP_ID) {
    let timeStamp = time()
    let cache;
    let goodInfo = GOODS_INFO
    let menusList = MENUS_LIST
    let shopId = SHOP_ID
    let cacheItem = {
        goodInfo, menusList, shopId, timeStamp
    }
    if (sessionStorage.cache) {
        cache = JSON.parse(sessionStorage.cache)
        cache.forEach(item => {
            if (item.shopId = cacheItem.shopId) {
                item = cacheItem
            }
        })
    } else {
        cache = []
        cache.push(cacheItem)
    }
    localStorage.cache = JSON.stringify(cache)
}

function deleteCache() {
    let shopId = sessionStorage.shopId;
    let cache = JSON.parse(localStorage.cache);
    cache.forEach((item, index) => {
        if (item.shopId == shopId) {
            cache.splice(index, 1)
        }
    })
    localStorage.cache = JSON.stringify(localStorage.cache)
}

export { getCache, saveCache,deleteCache }