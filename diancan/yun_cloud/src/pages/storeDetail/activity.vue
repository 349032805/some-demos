<template>
    <div class="dish-activity">
        <div class="activity-item" :class="actType(item.logoName)" v-for="item in orderActivityOutVoList">
            {{item.activityName}}
        </div>
    </div>
</template>

<script>
export default {
    props: {
        orderActivityOutVoList: Array
    },
    methods: {
        actType(word){
            if(word == '新'){
                return 'xin'
            }
            if(word=='减'){
                return 'man-jian'
            }
            if(word == '折'||word=='惠'){
                return 'man-zhe'
            }
            if(word == "赠"||word=="送"){
                return 'man-zeng'
            }
        }
    }
}
</script>

<style lang="less">
    @import '../../style/color.less';
    .dish-activity{
        .activity-item{
            margin-top: .22rem;
            padding-left: .45rem;
        }
        .xin{
            background: url('../../assets/xin.png') no-repeat left .05rem;
            background-size:.28rem .28rem;
        }
        .man-zeng{
            background: url('../../assets/man-zeng.png') no-repeat left .05rem;
            background-size:.28rem .28rem;
        }
        .man-zhe{
            background: url('../../assets/man-zhe.png') no-repeat left .05rem;
            background-size:.28rem .28rem;
        }
        .man-jian{
            background: url('../../assets/jian.png') no-repeat left .05rem;
            background-size:.28rem .28rem;
        }
        .pull-right{
            color: @colorg;
        }
        
    }
</style>
