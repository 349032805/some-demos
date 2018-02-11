import axios from '@/config/axios';
import { getPackagePrice } from './countPackagePrice';
export async function getExtraInfo(baseUrl, shopId) {
    let box = {};
    box.demandList = [];
    box.serviceType = 0;
    box.serviceTotalPrice = 0;
    box.serviceUnitPrice = 0;
    box.selectTimeList = [];

    const SUCCESS = 200;

    await axios("get", baseUrl + '/getOrderExtraInfo', { shopId: shopId }).then((response) => {
        if (response.code == SUCCESS) {
            let model = response.model;

            let demandList = model.menuItemAskOutVoList;
            if (model.preOrderTimeList) {
                model.preOrderTimeList.forEach(item => {
                    box.selectTimeList.push(item);
                })
            }


            demandList.forEach(item => {
                // this.$set(item, 'flag', false);
                item.flag = false;
                box.demandList.push(item);
            });

            if (model.orderServiceFeeOutVoList) {
                let serviceList = model.orderServiceFeeOutVoList;
                if (serviceList.length > 0) {
                    serviceList.forEach(item => {
                        box.serviceType = item.type; //服务费类型

                        //1,没有服务费
                        if (item.type == 1 || item.type == '1') {
                            console.log("没有服务费")
                        }

                        //2,固定费用
                        if (item.type == 2 || item.type == '2') {
                            box.serviceTotalPrice = item.unitPrice;
                            box.serviceUnitPrice = item.unitPrice;
                        }

                        //3.拿菜品的打包盒单价乘以打包盒数量,再乘以菜品数量
                        //最后还要加上赠送菜品的打包盒费用
                        if (item.type == 3 || item.type == '3') {
                            box.serviceTotalPrice = getPackagePrice();
                        }
                    })

                }
            }

        }
    })

    return box;
};
