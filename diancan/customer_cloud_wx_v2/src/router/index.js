import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

//路由异步切换示例
const home = r => require.ensure([], () => r(require('../pages/home/home')), 'home')
const personCenter = r => require.ensure([], () => r(require('../pages/personCenter/personCenter')), 'personCenter')
const updateInfo = r => require.ensure([], () => r(require('../pages/updateInfo/updateInfo')), 'updateInfo')
const changeAddress = r => require.ensure([], () => r(require('../pages/changeAddress/changeAddress')), 'changeAddress')
const searchAddress = r => require.ensure([], () => r(require('../pages/searchAddress/searchAddress')), 'searchAddress')
const updateSex = r => require.ensure([], () => r(require('../pages/updateSex/updateSex')), 'updateSex')
const updateNickname = r => require.ensure([], () => r(require('../pages/updateNickname/updateNickname')), 'updateNickname')
const myCollection = r => require.ensure([], () => r(require('../pages/myCollection/myCollection')), 'myCollection')
const Menus = r => require.ensure([], () => r(require('../pages/menus/menus')), 'menus')
const searchOrder = r => require.ensure([], () => r(require('../pages/searchOrder/searchOrder')), 'searchOrder')
const myOrder = r => require.ensure([], () => r(require('../pages/myOrder/myOrder')), 'myOrder')
const orderDetail = r => require.ensure([], () => r(require('../pages/orderDetail/orderDetail')), 'orderDetail')
const serviceRecord = r => require.ensure([], () => r(require('../pages/serviceRecord/serviceRecord')), 'serviceRecord')
const bindPhone = r => require.ensure([], () => r(require('../pages/bindPhone/bindPhone')), 'bindPhone')
const givingAssessment = r => require.ensure([], () => r(require('../pages/givingAssessment/givingAssessment')), 'givingAssessment')
const storeDetail = r => require.ensure([], () => r(require('../pages/storeDetail/storeDetail')), 'storeDetail')
const assessmentDetail = r => require.ensure([], () => r(require('../pages/assessmentDetail/assessmentDetail')), 'assessmentDetail')
const dishDetail = r => require.ensure([], () => r(require('../pages/dishDetail/dishDetail')), 'dishDetail')
const storeLocation = r => require.ensure([], () => r(require('../pages/storeLocation/storeLocation')), 'storeLocation')
const shareStore = r => require.ensure([], () => r(require('../pages/shareStore/shareStore')), 'shareStore')
const error = r => require.ensure([], () => r(require('../pages/error/error')), 'error')
const noResturant = r => require.ensure([], () => r(require('../pages/error/noResturant')), 'noResturant')
const confirmOrder = r => require.ensure([], () => r(require('../pages/confirm/confirmOrder')), 'confirmOrder')
const scan = r => require.ensure([], () => r(require('../scan/scan')), 'scan')
export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      component: home,
    },
    {
      path: '/searchOrder',
      name: "searchOrder",
      component: searchOrder
    },
    {
      path: '/changeAddress',
      name: "changeAddress",
      component: changeAddress,
      children: [
        {
          path: '/searchAddress',
          name: "searchAddress",
          component: searchAddress,
        }
      ]
    },
    {
      path: '/orderDetail/:orderId',
      name: 'orderDetail',
      component: orderDetail,
    },
    {
      path: '/givingAssessment/:orderId/:fsShopGuid',
      name: "givingAssessment",
      component: givingAssessment
    }, {
      path: '/assessmentDetail/:orderId',
      name: "assessmentDetail",
      component: assessmentDetail
    },
    {
      path: '/personCenter',
      name: 'personCenter',
      component: personCenter
    },
    {
      path: '/updateInfo',
      name: 'updateInfo',
      component: updateInfo
    },
    {
      path: '/updateSex',
      name: 'updateSex',
      component: updateSex
    },
    {
      path: '/updateNickname',
      name: 'updateNickname',
      component: updateNickname
    },
    {
      path: '/myCollection',
      name: 'myCollection',
      component: myCollection
    },
    {
      path: '/menus/:shopId',
      name: 'menus',
      component: Menus,
      meta: {
        keepAlive: true
      }
    },
    {
      path: '/menus/:shopId/:token',
      name: 'menusScan',
      component: Menus,
      meta: {
        keepAlive: true
      },
      children: [{
        path: '/dishDetail',
        name: "dishDetail",
        component: dishDetail,
        meta: {
          keepAlive: true
        }
      }]
    },
    {
      path: '/myOrder',
      name: 'myOrder',
      component: myOrder
    },
    {
      path: '/serviceRecord',
      name: 'serviceRecord',
      component: serviceRecord
    },
    {
      path: '/bindPhone',
      name: 'bindPhone',
      component: bindPhone
    },
    {
      path: '/storeDetail/:storeId',
      name: 'storeDetail',
      component: storeDetail
    },
    {
      path: '/storeLocation/:longitude/:latitude',
      name: 'storeLocation',
      component: storeLocation
    },
    {
      path: '/shareStore',
      name: 'shareStore',
      component: shareStore
    },
    {
      path: '/error',
      name: 'error',
      component: error
    },
    {
      path: '/noResturant',
      name: 'noResturant',
      component: noResturant
    },
    {
      path: '/confirmOrder',
      name: 'confirmOrder',
      component: confirmOrder
    },
    {
      path: '/scan',
      name: 'scan',
      component: scan
    }
  ]
})
