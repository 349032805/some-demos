<template>
    <div class="history-search">
        <div class='search-header' v-if='list.length>0'>
            <span>历史搜索</span>
            <img @click="cleanHistory" src="../../assets/02.png" alt="">
        </div>
        <ul class='search-body'>
            <li v-for="item in list" @click="enterStore(item.fsShopGUID)">{{item.fsShopName}}</li>
        </ul>
    </div>
</template>

<script>
export default {
    name: "historySearch",
    created() {
        if (localStorage.historyStore) {
            let historyStore = JSON.parse(localStorage.historyStore)
            this.list = historyStore
        }
    },
    data() {
        return {
            list: []
        }
    },
    methods: {
        //删除历史记录
        cleanHistory() {
            var _this = this
            this.$crib.confirm.show({
                content: '确定要删除历史记录？',
                onConfirm() {
                    localStorage.removeItem("historyStore")
                    _this.list = []
                },
                confirmStyle :{
                    color:'#06C1AE'
                }
            })

        },
        enterStore(fsShopGUID) {
            this.$router.push({ name: 'menus', params: { shopId: fsShopGUID } })
        }
    }

}
</script>

<style lang='less'>
@import './historySearch.less';
</style>


