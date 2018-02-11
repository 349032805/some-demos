<template>
    <div class="curAddress">
        <p class="text">当前位置</p>
        <p class="address">
            <span class="innerAddress">{{ADDRESS? ADDRESS : "无法获取当前位置"}}</span>
            <span   :class='[ADDRESS == "定位中..."?"textColor":""]' class="rget" @click="rGetAddress"><spinner style='margin-right:5%;' color="#666666" type='flower' v-if='ADDRESS == "定位中..."'></spinner><img v-else src="../../assets/ReLocation.png" alt=""><span>{{text}}</span></span>
               
        </p>
    </div>
</template>

<script>
import {Spinner} from '@/crib-zk'; 
import { getLocation } from '@/config/common.js'
import { mapState } from 'vuex'
export default {
    //缓存当前地址，如果经纬度为空的时候，就把当前地址设为空，否则就把vux存的地址为session的地址
    activated () {
       if(!sessionStorage.LOCATION){
           this.$store.commit('SAVE_ADDRESS','')
       }else{
           this.$store.commit('SAVE_ADDRESS',sessionStorage.ADDRESS)
       }
    },
    components : {
      Spinner
    },
    data() {
        return {
            text: "重新定位",
        }
    },
    computed: {
        ...mapState([
            'ADDRESS'
        ]),
    },
    methods: {
        //重新定位
        rGetAddress() {
            this.text = "正在定位"
            this.$store.commit('SAVE_ADDRESS','定位中...')
            getLocation((lng, lat) => {
                let location = {
                    lat: lat,
                    lng: lng,
                }
                this.text = "重新定位"
                this.$store.dispatch('GET_ADDRESS', location)
            }, () => {
                this.text = "重新定位"
                this.$store.commit('SAVE_ADDRESS','')
                this.$toast('无法获取当前位置,请搜索位置')
            })
        }
    }

}
</script>

<style lang='less'>
@import './currentCountry.less';

</style>


