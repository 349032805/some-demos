
function concat (GOODS_INFO) {
    let falg_fsitemid = ""
    let array = []
    GOODS_INFO.forEach((item,index)=>{
        let info = item.info
        let count = item.count
        let {fsorderunit,fsitemid,fdsaleprice} = info;
        if(falg_fsitemid == fsitemid){
             array[array.length-1].count+=count
           
        }else{
            let obj = {fsorderunit,fsitemid,fdsaleprice,count};
            falg_fsitemid = fsitemid
            array.push(obj)
        }
    })
    return array

}
export {concat}