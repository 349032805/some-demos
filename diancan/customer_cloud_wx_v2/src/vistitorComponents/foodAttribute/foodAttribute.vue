<template>
    <div class="attr-out-box">
        <div class="food-attribute">
            <div class="food-title clearfix"><span class="close pull-right" @click.stop.prevent="closeBox()"><span class="icon-error"></span></span>{{food.fsitemname}}</div>
            <div class="food-content">
                 <div class="rule-item" v-if="food.menuItemUnitList&&food.menuItemUnitList.length>0">
                    <p>选择规格</p>
                    <div class="label-box">
                        <label v-for="(item,index) in food.menuItemUnitList" @click.stop.prevent="selectUnit(item,index)" :key="index">
                            <input type="radio" :checked="index==0||item.fsorderunit==food.selAttrObj.fsorderunit" :disabled="item.fistatusUnit!=1" :value="item.fsorderunit" name="taste">
                            <span>{{item.fsorderunit}}</span>
                            <span class="sell-out" v-if="item.fistatusUnit!=1">售罄</span>
                        </label>
                    </div>
                </div>
                <div class="rule-item" v-if="isHasAskList">
                    <div v-for="(item,index) in food.menuItemUnitList[unitIndex].askList" :key="index" >
                        <p v-if="item.menuItemAskOutVoList.length>0">{{item.fsAskGpName}}</p>
                        <div class="label-box" v-if="item.menuItemAskOutVoList.length>0">
                            <label v-for="(subItem,subIndex) in item.menuItemAskOutVoList" @click.stop.prevent="selectAsk(food.menuItemUnitList[unitIndex],subItem)" :key="subIndex">
                                <input type="checkbox" :checked="subItem.checked" name="classify">
                                <span>{{subItem.fsaskname}}</span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="food-footer clearfix" v-if="food.menuItemUnitList&&food.menuItemUnitList.length>0">
                <div class="btn-box pull-right">
                    <numberControl :item="food" :isattr="true" @closeBox="closeBox()" :itemExtra="food.menuItemUnitList[unitIndex]" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
                </div>
                <p>¥{{nowPrice.bargainPrice?nowPrice.bargainPrice.toFixed(2):nowPrice.fdsaleprice.toFixed(2)}}/{{food.selAttrObj.fsorderunit}} 
                    <span class="now-price" v-if="food.selAttrObj.bargainPrice">¥{{nowPrice.fdsaleprice.toFixed(2)}}</span>
                </p>
            </div>
        </div>
        <div class="backdrop" v-show="showDeskDrop"></div>
    </div>
</template>
<script>
    import numberControl from '@/vistitorComponents/numControl/numControl.vue'
    export default {
        props : {
            food:  {
                type: Object,
                default:{}
            }
        },
        data() {
            return {
                unitIndex : 0,//当前选中的规格类型
                showDeskDrop : true,
                isHasAskList:false
            }
        },
        components : {
            numberControl
        },
        watch: {
            showDeskDrop : function (val,oldval){
                if(!val){
                    this.showDeskDrop = !this.showDeskDrop;
                }
            },
            food:{
                handler: function (val, oldVal) {
                    let count=0;
                    let _this = this;
                    if(val.menuItemUnitList){
                        val.menuItemUnitList.forEach(function(v) {
                            if(v.count){
                                count+=v.count;
                            }
                        });
                        this.$set(val,'count',count);
                    }
                },
                deep: true
            }
        },
        computed: {
            nowPrice(){
                let price = {
                    fdsaleprice: this.food.selAttrObj.fdsaleprice,
                    bargainPrice: this.food.selAttrObj.bargainPrice
                }
                this.food.menuItemUnitList.forEach(function(item){
                    if(item.askList){
                        item.askList.forEach(function(ask){
                            ask.menuItemAskOutVoList.forEach(function(subAsk){
                                if(subAsk.checked){
                                    if(price.bargainPrice){
                                        price.bargainPrice += subAsk.fdaddprice;
                                    }
                                    price.fdsaleprice += subAsk.fdaddprice;
                                }
                            })
                        })
                    }
                })
                return price;
            }
        },
        methods : {
            selectUnit(item,index){
                this.unitIndex = index;
                this.food.selAttrObj = item;
            },
            selectAsk(unitItem,item){
                if(typeof item.checked=='undefined'){
					this.$set(item,'checked',true);
				}else{
					item.checked = !item.checked;
				}
                this.$emit("selectAsk",unitItem);
            },
            closeBox() {
                this.showDeskDrop = !this.showDeskDrop;
                this.$emit('closeAttributeBox');
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
    @import './foodAttribute.less';
</style>