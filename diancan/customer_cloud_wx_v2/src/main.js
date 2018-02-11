
import Vue from 'vue';
import App from './App';
import router from './router';
import FastClick from 'fastclick';
import http from '@/config/axios';
import store from '@/Vuex';
import { Toast } from 'mint-ui';
import { MessageBox } from 'mint-ui';
import { Indicator } from 'mint-ui';
import { Swipe, SwipeItem } from 'mint-ui';
import { InfiniteScroll } from 'mint-ui';
import { Actionsheet } from 'mint-ui';
import { Popup } from 'mint-ui';
import { Switch} from 'mint-ui';
import { Spinner } from 'mint-ui';
import {ConfirmPlugin} from '@/crib-zk';
import {AlertPlugin}  from '@/crib-zk'
import { Lazyload } from 'mint-ui';

Vue.use(Lazyload);
Vue.use(AlertPlugin)
Vue.use(InfiniteScroll);
 Vue.use(ConfirmPlugin);
Vue.component(Swipe.name, Swipe);
Vue.component(Swipe.name, Swipe)
Vue.component(SwipeItem.name, SwipeItem);
Vue.component(Actionsheet.name, Actionsheet);
Vue.component(Popup.name, Popup);
Vue.component(Switch.name, Switch);
Vue.component(Spinner.name, Spinner);
//把请求挂在全局vue实例下，可以直接在页面或者组件中用this.$http()来使用
Vue.prototype.$http = http
//把toast挂在vue实例下
Vue.prototype.$toast = Toast
Vue.prototype.$message = MessageBox
Vue.prototype.$indicator = Indicator
Vue.prototype.baseUrl = '/fastfood_wechat_v2'

Vue.config.productionTip = false

//去除300毫秒的点击延迟
if ('addEventListener' in document) {
  document.addEventListener('DOMContentLoaded', function() {
    FastClick.attach(document.body);
  }, false);
}


new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})


new Vue({
  el:'title',
  store,
  computed : {
    title () {
      return this.$store.state.TITLE
    }
  }
})
