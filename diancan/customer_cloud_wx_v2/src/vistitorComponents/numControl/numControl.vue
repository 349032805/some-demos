<template>
    <div class="btn-dish-box">
        <a href="javascript:;" class="btn-add" :class="{'btn-gray':this.$store.state.SHOP_STATUS&&(this.$store.state.SHOP_STATUS==0||this.$store.state.SHOP_STATUS==2)}" @click.stop.prevent="toggleNum($event,1)" v-if="(item.isShowUnit==1?(!item.count||item.count==0):(!itemExtra.count||itemExtra.count==0))&&(!isattr?item.fiiscout==0:itemExtra.fistatusUnit==1)">添加</a>
        <a href="javascript:;" class="btn-add btn-gray" v-if="!isattr?item.fiiscout==1:itemExtra.fistatusUnit!=1">已售罄</a>
        <div v-if="(item.isShowUnit==1?item.count>0:itemExtra.count>0)&&item.fiiscout==0" :class="{'btn-add-box':true,'cart-btn-box':iscart}">
            <span class="btn-minus" @click.stop.prevent="toggleNum($event,3)">－</span>
            <input type="tel" v-if="item.isShowUnit==1" readonly maxlength="3" class="dish-count" v-model.trim="item.count">
            <input type="tel" v-else maxlength="3" readonly class="dish-count" v-model.trim="itemExtra.count">
            <!--<input type="tel" v-if="item.isShowUnit==1" readonly maxlength="3" class="dish-count" v-model.trim="item.count" @click="toggleNum($event,4)">
            <input type="tel" v-else maxlength="3" readonly class="dish-count" v-model.trim="itemExtra.count" @click="toggleNum($event,4)">-->
            <span class="btn-plus" @click.stop.prevent="toggleNum($event,2)">＋</span>
        </div>
    </div>
</template>
<script>
    import Vue from 'vue'
    export default {
        props : {
            item : Object,
            itemExtra : Object,
            isattr:false,//判断是属性调用的组件，用于点滴一次关闭属性弹出框
            iscart:false//购物车
        },
        methods : {
            enterNum(event){
                this.$emit('openKeyboard',event)
            },
            toggleNum(event,type){
                //餐厅不营业
                let _stopStatus = this.$store.state.SHOP_STATUS;
                if(_stopStatus&&(_stopStatus==0||_stopStatus==2)) return;
                //type 1添加，2加，3减
                let _dishesId = this.item.fsitemid;
                let _itemUnit = this.itemExtra.fsorderunit;
                let isFirClick = true;
                let nowItem = {};//目前操作的对象
                //判断是哪一个对象，isShowUnit==0是有属性的菜品
                if(this.item.isShowUnit==0){
                    nowItem = this.itemExtra;
                }else{
                    nowItem = this.item;
                }
                if(type==1){
                    if(!nowItem.count){
                        Vue.set(nowItem, 'count', 1);
                    }
                    if(this.isattr){
                        this.$emit("closeBox");
                    }
                }else if(type==2){
                    nowItem.count++;
                    isFirClick = false;
                }else if(type==3){
                    if(!nowItem.count) return;
                    nowItem.count--;
                }
                let obj = {
                    firClick:isFirClick,
                    target:event.target,
                    dishesId:_dishesId,
                    itemUnit:_itemUnit,
                    count:nowItem.count
                }
                if(type==4){
                    this.$emit('openKeyboard',obj)
                }else{
                    this.$emit("drop",obj);
                }
                
            }
        }
    }
</script>
<style lang="less">
    @import "./numControl.less";
</style>