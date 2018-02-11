<template>
    <div>
         <div class="img-box pull-left" @click="showDishDetial(item)">
            <img v-if="item.fsimageurl" :src="cutImg(item.fsimageurl,134,134)">
            <img v-else src="../../assets/nodish-small.png">
            <div class="youhui" v-if="itemExtra.menuItemActivity">{{itemExtra.menuItemActivity.bargainName}}</div>
        </div>
        <h3>{{item.fsitemname}}</h3>
        <p>已售{{item.soldNum}}份</p>
        <p class="price">￥{{itemExtra.bargainPrice?itemExtra.bargainPrice:itemExtra.fdsaleprice}}/{{itemExtra.fsorderunit}}<span class="now-price" v-if="itemExtra.bargainPrice >0">¥{{itemExtra.fdsaleprice}}</span></p>
        <div class="number-control">
            <a href="javascript:;" class="btn-add" @click.stop.prevent="showAttribute(item)" v-if="item.isShowUnit==0">选规格</a>
            <numberControl :item="item" :itemExtra="itemExtra" v-if="item.isShowUnit==1" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
            <span class="num" v-show="item.attrCount>0">{{item.attrCount}}</span>
        </div>
    </div>
</template>
<script>
    import Vue from 'vue'
    import cutImg from '@/config/cutImg.js'
    import numberControl from '@/vistitorComponents/numControl/numControl.vue'
    export default {
        props : {
            item : Object,
            itemExtra : Object,
            menusid:String
        },
        components : {
            numberControl
        },
        mixins: [cutImg],
        methods : {
            showDishDetial(item){
                sessionStorage.setItem("detailMenusid",this.menusid);
                this.$emit('showDishDetial',item);
            },
            showAttribute(item){
                sessionStorage.setItem("attrMenusid",this.menusid);
                this.$emit('showAttribute',item);
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
    @import './dish.less';
</style>