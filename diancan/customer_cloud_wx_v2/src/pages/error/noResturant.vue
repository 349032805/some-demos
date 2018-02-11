<template>
    <div class="error-page">
        <img src="../../assets/noresturant.png">
        <p v-if="status==1">该餐厅不存在</p>
        <p v-if="status==2">该餐厅已停止服务</p>
    </div>
</template>
<script>
export default {
    data() {
        return {
            status: 1
        }
    },
    created() {
        this.$store.commit('SAVE_TITLE', '百味云点餐系统');
        let _status = this.$route.query.status;
        if (_status) {
            this.status = this.$route.query.status;
        }
    }
}
</script>

<style>
.error-page {
    text-align: center;
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    margin: auto;
    width: 200px;
    height: 120px;
    color: #999;
}

.error-page img {
    width: 60px;
    height: 60px;
    margin-bottom: 15px;
}
</style>
