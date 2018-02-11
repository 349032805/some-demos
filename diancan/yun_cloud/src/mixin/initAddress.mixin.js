
import Vue from 'vue';
let initAddress = {
    created() {
        let sign;
       
        if(!sessionStorage.sign) {
           sign = this.$route.query.sign
           sessionStorage.sign = sign
        }else{
           sign = sessionStorage.sign
        }
        let baseUrl = "";
        if (sign == "wx") {
            baseUrl = "/fastfood_wechat_v2";
        } else {
            baseUrl = "/fastfood_alipay_v2";
        }
        Vue.prototype.$sign = sign
        Vue.prototype.baseUrl = baseUrl
    }
}

export {initAddress}