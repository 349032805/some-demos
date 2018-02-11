<template>
    <div class="marquee">
        <marquee>
            <marquee-item v-for="(item,index) in shopActivityOutVoList" :key="index">
                <img :src="imgFilter(item.logoName)" alt="" class='tipimg'>
                <span>{{item.activityName}}</span>
            </marquee-item>
        </marquee>
        <div class="marquee-tip">
            <span>{{shopActivityOutVoList && shopActivityOutVoList.length}}个活动</span>
            <img src="../../../../assets/activityArrow.png" alt="">
        </div>
    </div>
</template>

<script>
import Marquee from '@/crib-zk/components/marquee/index.vue'
import MarqueeItem from '@/crib-zk/components/marqueeItem/index.vue'
import zhe from '@/assets/zhe.png'
import jian from '@/assets/jian.png'
import xin from '@/assets/xin.png'
import zeng from '@/assets/zeng.png'
export default {
    props: {
        shopActivityOutVoList: {
            type: Array,
            require: true,
        }
    },
    methods: {
        imgFilter(logoName) {
            if (logoName == '新') {
                return xin
            }
            if (logoName == '减') {
                return jian
            }
            if (logoName == '折' || logoName == '惠') {
                return zhe
            }
            if (logoName == "赠" || logoName == "送") {
                return zeng
            }
        }
    },
    components: {
        Marquee,
        MarqueeItem
    },
    data() {
        return {
            number: 10
        }
    }
}
</script>
<style lang='less' scoped>
@import './menuActivity.less';
</style>




