<template>
    <div class="bg-layer showModal-unit" v-show="showShare">
        <div class="share-arrow">
            <img src="../../assets/share_arrow.png">
        </div>

        <!-- 分享的模态框 -->
        <transition name="bounceIn">
            <div class="share-modal" v-show="showShare">
                <div class="modal-store-banner" :style="[styleObj]">
                    <!-- <img src="../../assets/default_store_banner.png" class="banner" v-if="!store.fsPromote"> -->
                    <!-- <img :src="store.fsPromote" class="banner" v-if="store.fsPromote"> -->
                    <img src="../../assets/share_shape.png" class="share-shape">

                    <img src="../../assets/store_logo2.png" class="store-logo" v-if="!store.fsLogo"> 
                    <!-- <img :src="store.fsLogo" v-if="store.fsLogo" class="store-logo"> -->
                    <div class="store-logo" :style="[logoStyle]" v-if="store.fsLogo"></div>
                </div>

                <div class="store-info">
                    <div class="name-bus">
                        <p class="name">{{store.fsShopName}}</p>
                        <div class="mode">
                            <span class="mode-item" v-if="store.orderType==2">
                                <i></i>
                                <span>提前点餐</span>
                            </span>
                            <span class="mode-item" style="margin-right: 0;">
                                <i></i>
                                <span>打包</span>
                            </span>
                        </div>
                    </div>

                    <!-- 分割线 -->
                    <div class="split-line">
                        <div class="bottom-line"></div>
                    </div>

                    <div class="intro" v-if="store.fsShopDesc">{{store.fsShopDesc}}</div>
                </div>

                <div class="recommend">
                    <span>点击右上角，将餐厅推荐给朋友</span>

                </div>
            </div>
        </transition>

        <div class="share-close" @click="$emit('update:showShare',false)">
            <img src="../../assets/share_close.png">
        </div>

    </div>
</template>

<script>
export default {
    name: 'showModal',
    props: {
        showShare: Boolean,
        store: Object
    },
    computed: {
        styleObj() {
            if (!this.store.fsPromote) {
                return { backgroundImage: 'url(' + require('../../assets/default_store_banner.png') + ')' }
            } else {
                return { backgroundImage: `url(${this.store.fsPromote})` }
            }
        },
        logoStyle() {
            if (!this.store.fsLogo) {
                return { backgroundImage: 'url(' + require('../../assets/store_logo2.png') + ')' }
            } else {
                return { backgroundImage: `url(${this.store.fsLogo})` }
            }
        }
    }
}
</script>

<style lang="less">
@import './shareModal.less';
</style>
