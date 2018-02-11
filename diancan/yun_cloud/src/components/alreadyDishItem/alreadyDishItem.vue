<template>
    <div class="order-list-content">
        <div class="order-item" v-for="item in orderMenuitemOutVoList">
            <div class="clearfix">
                <div class="price">
                    <div v-if="!isConfirm">
                        <p v-if="item.isDiscount==2">
                            ¥{{item.price*item.number - item.discountPrice}}
                        </p>
                        <p v-else>
                            ¥{{item.discountPrice==item.price?item.price*item.number:item.discountPrice*item.number}}
                        <p>
                        <p class="original-price" v-if="item.discountPrice!=item.price">¥{{item.price*item.number}}</p> 
                    </div>
                    <div v-else>
                         ¥{{item.discountPrice==item.price?item.price:item.discountPrice}}
                        <p class="original-price" v-if="item.discountPrice!=item.price">¥{{item.price}}</p> 
                    </div>
                </div>
                <div class="name">
                    <img v-if="item.isDiscount==2" src="../../assets/zhe.png">
                    {{item.fsitemname}}
                    <p class="remark" v-if='item.orderMenuItemRequirementOutVoList'>
                        {{item.unit}}
                        <span>
                            <span v-for='item2 in item.orderMenuItemRequirementOutVoList'> / {{item2.fsaskname}}</span>
                        </span>
                    </p>
                </div>
                <div class="quantity">×{{item.number}}</div>
            </div>
            <div class="song" v-if='item.isGive == 1 ||!item.isGive' v-for='item2 in item.orderGiveMenuitemOutVoList'>
                <div class="name">
                    <img src="../../assets/zeng.png">{{item2.fsitemname}}
                </div>
                <div class="quantity">×{{item2.number}}</div>
                <div class="original-price">¥{{item.orderGiveMenuitemOutVoList[0].number*item.price | addZero}}</div>
            </div>
        </div>
        <!--<div class="order-item" v-for="item in orderMenuitemOutVoList">
            <div class="price">¥{{item.price}}</div>
            <div class="name">
                {{item.fsitemname}}
                <p class="remark">
                    <span v-if='item.orderMenuItemRequirementOutVoList'>
                        <span v-for='item2 in item.orderMenuItemRequirementOutVoList'>/{{item2.fsaskname}}</span>
                    </span>
                </p>
            </div>
            <div class="quantity">×{{item.number}}</div>
        </div>-->
        <!--<div class="order-item">
            <div class="name">
                无规格的香菜鸡肉无规格的香菜鸡肉无规格的香菜鸡肉
            </div>
            <div class="quantity">×1</div>
            <div class="price">
                ¥158.08
                <p class="original-price">¥28.8</p>
            </div>
        </div>
        <div class="order-item">
            <div class="clearfix">
                <div class="name">
                    买一送一有规格的菜品
                    <p class="remark">大份／微辣</p>
                </div>
                <div class="quantity">×1</div>
                <div class="price">¥158.08</div>
            </div>
            <div class="song">
                <div class="name">
                    <img src="../../assets/zeng.png">紫甘蓝金枪鱼色拉
                </div>
                <div class="quantity">×1</div>
                <div class="original-price">¥288.8</div>
            </div>
        </div>
            <div class="order-item">
            <div class="name">
                买一送一无规格的菜品
            </div>
            <div class="quantity">×1</div>
            <div class="price">
                ¥158.08
            </div>
            <div class="song">
                <div class="name">
                    <img src="../../assets/zeng.png">紫甘蓝金枪鱼色拉
                </div>
                <div class="quantity">×1</div>
                <div class="original-price">¥288.8</div>
            </div>
        </div>
        <div class="order-item">
            <div class="name">
                <img src="../../assets/zhe.png">第二件半价的菜
                <p class="remark">大份／微辣／不要葱</p>
            </div>
            <div class="quantity">×1</div>
            <div class="price">
                ¥158.08
                <p class="original-price">¥288.8</p>
            </div>
        </div>-->
    </div>
</template>

<script>
export default {
    props: {
        orderMenuitemOutVoList: Array,
        isConfirm : {
            type: Boolean,
            default: true
        }
    },
    filters : {
        addZero (value) {
            value = value.toFixed(2)
            return value
        }
    }
}
</script>

<style lang="less">
  @import './alreadyDishItem.less';
</style>
