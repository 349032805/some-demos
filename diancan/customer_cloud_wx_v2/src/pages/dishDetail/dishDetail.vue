<template>
    <div class="dish-detail">
        <div class="dish-img">
            <img v-if="dishDetail.fsimageurl" :src="cutImg(dishDetail.fsimageurl,750,750)">
            <img v-else src="../../assets/nodish-big.png">
        </div>
        <div class="dish-text">
            <h3>{{dishDetail.fsitemname}}</h3>
            <p>已售{{dishDetail.soldNum}}份</p>
            <span class="dish-price">¥{{unitList.bargainPrice?unitList.bargainPrice:unitList.fdsaleprice}}/{{unitList.fsorderunit}}
                <span class="now-price" v-if="unitList.bargainPrice>0">¥{{unitList.fdsaleprice}}</span></span>
            <div class="order-dish-box pull-right">
                <a href="javascript:;" class="btn-add" @click.stop.prevent="showAttribute(dishDetail)" v-if="dishDetail.isShowUnit==0">选规格</a>
                <numberControl :item="dishDetail" :itemExtra="unitList" v-if="dishDetail.isShowUnit==1" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
                <span class="num" v-show="dishDetail.attrCount>0">{{dishDetail.attrCount}}</span>
            </div>
        </div>
        <div class="dish-dis" v-if="dishDetail.fsitemdesc">
            <h3>菜品信息</h3>
            <p>{{dishDetail.fsitemdesc}}</p>
        </div>
        <div class="dish-star">
            <p class="pull-right dis"><span v-if="dishDetail.likeNum>0"><span class="count">{{dishDetail.likeNum}}人</span>喜欢</span><span v-else>暂无</span></p>
            <h3>菜品欢迎程度</h3>
        </div>
    </div>
</template>
<script>
    import cutImg from '@/config/cutImg.js'
    import numberControl from '@/vistitorComponents/numControl/numControl.vue'
    export default {
        props : {
            dishDetail : Object,
            unitList : Object,
            menusid:String
        },
        components : {
            numberControl
        },
        mixins: [cutImg],
        methods : {
            showAttribute(food) {
                sessionStorage.setItem("attrMenusid",this.menusid);
                this.$emit("showAttribute",food);
            },
            drop(target){
                this.$emit('drop',target);
            },
            openKeyboard(obj){
                this.$emit('openKeyboard',obj);
            }
        }
    }
</script>
<style lang="less">
    @import './dishDetail.less';
</style>