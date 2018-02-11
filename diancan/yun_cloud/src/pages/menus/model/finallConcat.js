function finallConcat (goodsInfo,goodsInfoMoney) {
    goodsInfoMoney.forEach((item,index)=>{
        goodsInfo[index].info = Object.assign(goodsInfo[index].info,item)
    })
    return goodsInfo
}

export {finallConcat}