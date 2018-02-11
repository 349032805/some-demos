<template>
    <div class="attention">
        <div class="attention-inner">
            <h1>A3102</h1>
            <p>请牢记就餐号，凭号取餐</p>
            <img src="../../../assets/attention.png">
            <div class="code">
                <img src="../../../assets/twocode.jpg">
                <div class="code-text">
                    <p>长按扫码</p>
                    <p>获取实时订单动态</p>
                </div>
            </div>
        </div>
        <div class="close" @click="closeAttention">
            <img src="../../../assets/share_close.png">
        </div>
    </div>
</template>

<script>
export default {
    methods:{
        closeAttention(){
            this.$parent.dialogShow = !this.$parent.dialogShow;
        }
    }
}
</script>

<style lang="less">
    @import './attention.less';
</style>
