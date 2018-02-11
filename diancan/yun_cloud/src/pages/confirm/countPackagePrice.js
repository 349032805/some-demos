import { Number } from "@/utils/money.js";
export function getPackagePrice() {

    let serviceTotalPriceAll = 0;
    let newDishList = sessionStorage.newDishList;
    newDishList = JSON.parse(newDishList);

    //已点菜品的打包费
    newDishList.forEach(dish => {
        if (dish.fdBoxPrice > 0) {
            serviceTotalPriceAll += Number(dish.fdBoxPrice * dish.fiBoxQty * dish.count);
        }
    })

    //加赠送的菜品打包盒总费用
    let sendDishList = sessionStorage.sendDishList;
    if (sendDishList) {
        sendDishList = JSON.parse(sendDishList);
        if (sendDishList.length > 0) {
            sendDishList.forEach(sendDish => {
                if (sendDish.fdBoxPrice > 0) {
                    serviceTotalPriceAll += Number(sendDish.fdBoxPrice * sendDish.fiBoxQty * sendDish.count);
                }
            })
        }
    }


    serviceTotalPriceAll = Number(serviceTotalPriceAll.toFixed(2));
    console.log("serviceTotalPriceAll:" + serviceTotalPriceAll);
    return serviceTotalPriceAll;
};