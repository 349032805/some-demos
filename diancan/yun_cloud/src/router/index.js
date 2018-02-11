import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

//路由异步切换示例
const home = r => require.ensure([], () => r(require('../pages/home/home')), 'home')
const changeAddress = r => require.ensure([], () => r(require('../pages/changeAddress/changeAddress')), 'changeAddress')
const searchAddress = r => require.ensure([], () => r(require('../pages/searchAddress/searchAddress')), 'searchAddress')
const SearchOrder = r => require.ensure([], () => r(require('../pages/searchOrder/searchOrder')), 'searchOrder')
const PersonCenter = r => require.ensure([], () => r(require('../pages/personCenter/personCenter')), 'personCenter')
const MyOrder = r => require.ensure([], () => r(require('../pages/myOrder/myOrder')), 'myOrder')
const UpdateInfo = r => require.ensure([], () => r(require('../pages/updateInfo/updateInfo')), 'updateInfo')
const UpdateSex = r => require.ensure([], () => r(require('../pages/updateSex/updateSex')), 'updateSex')
const BindPhone = r => require.ensure([], () => r(require('../pages/bindPhone/bindPhone')), 'bindPhone')
const UpdateNickname = r => require.ensure([], () => r(require('../pages/updateNickname/updateNickname')), 'updateNickname')
const orderDetail = r => require.ensure([], () => r(require('../pages/orderDetail/orderDetail')), 'orderDetail')
const givingAssessment = r => require.ensure([], () => r(require('../pages/givingAssessment/givingAssessment')), 'givingAssessment')
const assessmentDetail = r => require.ensure([], () => r(require('../pages/assessmentDetail/assessmentDetail')), 'assessmentDetail')
const ServiceRecord = r => require.ensure([], () => r(require('../pages/serviceRecord/serviceRecord')), 'serviceRecord')
const MyCollection = r => require.ensure([], () => r(require('../pages/myCollection/myCollection')), 'myCollection')
const Menus = r => require.ensure([], () => r(require('../pages/menus/menus')), 'menus')
const SearchDishes = r => require.ensure([], () => r(require('../pages/searchDishes/searchDishes')), 'searchDishes')
const StoreDetail = r => require.ensure([], () => r(require('../pages/storeDetail/storeDetail')), 'storeDetail')
const DishDetail = r => require.ensure([], () => r(require('../pages/dishDetail/dishDetail')), 'dishDetail')
const ConfirmOrder = r => require.ensure([], () => r(require('../pages/confirm/confirmOrder')), 'confirmOrder')
const storeLocation = r => require.ensure([], () => r(require('../pages/storeLocation/storeLocation')), 'storeLocation')
const pay = r => require.ensure([], () => r(require('../pages/aliPay/pay')), 'pay')
const scan = r => require.ensure([], () => r(require('../scan/scan')), 'scan')
const shareStore = r => require.ensure([], () => r(require('../pages/shareStore/shareStore')), 'shareStore')
const userAgreement = r => require.ensure([], () => r(require('../pages/userAgreement/userAgreement')), 'userAgreement')
export default new Router({
    routes: [{
        path: '/',
        name: 'home',
        component: home,
    },
    {
        path: '/searchOrder',
        name: 'searchOrder',
        component: SearchOrder
    },
    {
        path: '/pay/:orderId',
        name: "pay",
        component: pay
    },
    {
        path: '/shareStore',
        name: 'shareStore',
        component: shareStore
    },
    {
        path: '/scan',
        name: 'scan',
        component: scan
    },
    {
        path: '/personCenter',
        name: 'personCenter',
        component: PersonCenter
    },
    {
        path: '/updateInfo',
        name: 'updateInfo',
        component: UpdateInfo
    },
    {
        path: '/updateSex',
        name: 'updateSex',
        component: UpdateSex
    },
    {
        path: '/updateNickname',
        name: 'updateNickname',
        component: UpdateNickname
    },
    {
        path: '/myCollection',
        name: 'myCollection',
        component: MyCollection
    },
    {
        path: '/myOrder',
        name: 'myOrder',
        component: MyOrder
    },
    {
        path: '/serviceRecord',
        name: 'serviceRecord',
        component: ServiceRecord
    },
    {
        path: '/bindPhone',
        name: 'bindPhone',
        component: BindPhone
    },
    {
        path: '/userAgreement2',
        name: 'userAgreement2',
        component: userAgreement
    },
    {
        path: '/storeDetail/:shopId',
        name: 'storeDetailSingle',
        component: StoreDetail
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
        path: '/givingAssessment/:orderId/:fsShopGuid',
        name: "givingAssessment",
        component: givingAssessment
    }, {
        path: '/assessmentDetail/:orderId',
        name: "assessmentDetail",
        component: assessmentDetail
    },
    {
        path: '/orderDetail/:orderId',
        name: 'orderDetail',
        component: orderDetail,
    },
    {
        path: '/confirmOrderBack',
        name: 'confirmOrderBack',
        component: ConfirmOrder
    },
    {
        path: '/menus/:shopId/:token',
        name: 'menus',
        component: Menus,
        children: [{
            path: '/menus/:shopId/:token/searchDishes',
            name: 'searchDishes',
            component: SearchDishes
        }, {
            path: '/menus/:shopId/:token/storeDetail/:shopId',
            name: 'storeDetail',
            component: StoreDetail
        }, {
            path: '/menus/:shopId/:token/dishDetail',
            name: "dishDetail",
            component: DishDetail
        },
        {
            path: '/menus/:shopId/:token/confirmOrder',
            name: 'confirmOrder',
            component: ConfirmOrder
        },
        {
            path: '/menus/:shopId/:token/userAgreement',
            name: 'userAgreement',
            component: userAgreement
        },
        {
            path: '/menus/:shopId/:token/storeLocation/:longitude/:latitude',
            name: 'storeLocation',
            component: storeLocation
        }]
    }]
})