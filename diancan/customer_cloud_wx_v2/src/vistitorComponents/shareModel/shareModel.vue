<template>
    <div class="bg-layer" v-if='SHARE_MODEL == true'>
        <div class="share-arrow">
            <img src="../../assets/share_arrow.png">
        </div>
    
        <!-- 分享的模态框 -->
        <div class="share-modal">
            <div class="modal-store-banner" :style="[styleObj]">
                <img src="../../assets/share_shape.png" class="share-shape">
                <div class="store-logo" :style="[logoStyle]"></div>
            </div>
    
            <div class="store-info">
                <div class="name-bus">
                    <p class="name">{{store.fsShopName}}</p>
                    <div class="mode">
                        <span class="mode-item" v-if="store.orderType==2">
                            <i></i>
                            <span>预点</span>
                        </span>
                        <span class="mode-item">
                            <i></i>
                            <span>堂食</span>
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
    
        <div class="share-close" @click='close'>
            <img src="../../assets/share_close.png">
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
    props: {
        shareModel: false,
        store: Object
    },
    methods: {
        close() {
            this.$store.commit('CHANGE_SHARE_MODEL',false)
        }
    },
    computed: {
        ...mapState([
            'SHARE_MODEL'
        ]),
        styleObj() {
            if (!this.store.fsPromote) {
                return { backgroundImage: 'url(' + require('../../assets/default_store_banner.png') + ')' }
            } else {
                return { backgroundImage: `url(${this.store.fsPromote})` }
            }
        },
        logoStyle() {
            if (!this.store.fsLogo) {
                return { backgroundImage: 'url(' + require('../../assets/default_store.png') + ')' }
            } else {
                return { backgroundImage: `url(${this.store.fsLogo})` }
            }
        }
    }
}
</script>

<style lang='less'>
@import './shareModel.less';
</style>


