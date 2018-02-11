<template>
    <div class="searchAddress">
        <search class="crib-search" text='取消' :fixed='true' :autoFocus='true' placeholder='点击输入地址' :inputValue.sync='value' @on-submit='submit'></search>
        <div class="searchAddress-box">
            <div v-for="(item,index) in addressList" @click="chooseAddress(index)">
                <p class="name" v-html="item.match"></p>
                <p class="address">{{item.address}}</p>
            </div>
        </div>
    </div>
</template>

<script>
import { addressList } from '@/config/api.js'
import { mapState } from 'vuex'
import {Search} from "@/crib-zk"
export default {
    created() {
        this.$store.commit('SAVE_TITLE', '搜索')
        if (sessionStorage.CITY) {
            this.city = sessionStorage.CITY
        }else{
             this.city = this.CITY
        }
    },
    data() {
        return {
            value: "",
            addressList: [],
            style: `color:#06C1AE`,
            city :　""
        }
    },
    computed: {
        ...mapState([
            'CITY'
        ])
    },
    watch: {
        value(val, oldval) {
            this.keyup(val)
        }
    },
    components: {
        Search
    },
    methods: {
        submit () {
            this.$router.go(-1)
        },
        //选择地址
        chooseAddress(index) {
            sessionStorage.changeLocation = 1
            let address = this.addressList[index]
            let { name, location } = address
            this.$store.commit('SAVE_ADDRESS', name)
            sessionStorage.ADDRESS = name
            let newLocation =location.split(',')
            newLocation = {
                lng : newLocation[0],
                lat  : newLocation[1]
            }
            newLocation = JSON.stringify(newLocation)
            this.$store.commit('SAVE_LOCATION', newLocation)
            sessionStorage.LOCATION = newLocation
            this.$router.replace({ path: '/',query:{token:localStorage.token,type:sessionStorage.homeType}})
        },
        cancel() {
            this.value = ""
        },
        //请求地址列表
        keyup(value) {
            this.value = value
            addressList(value, this.city)
                .then(res => {
                    console.log(res)
                    if (res.status == '1') {
                        let result = res.pois
                        for (let i = 0; i < result.length; i++) {
                            let item = result[i]
                            let index = item.name.indexOf(this.value)
                            if (index !== -1) {
                                let match = item.name.replace(this.value, `<span style=${this.style}>${this.value}</span>`)
                                item.match = match
                            }else{
                                let match = item.name
                                item.match = match
                            }
                        }
                        this.addressList = result
                    }
                })
        }
    }

}
</script>

<style lang='less'>
@import './searchAddress.less';
.crib-search{
    z-index: 1000;
}
</style>


