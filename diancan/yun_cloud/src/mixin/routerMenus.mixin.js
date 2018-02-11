const routerMenus = {
    beforeRouteLeave(to, from, next) {
        console.log(to)
        if (to.name == 'menus') {
            this.$store.commit('SAVE_TITLE', '百味云点餐系统')
        }
        next()
    }
}

export {routerMenus}