<template>
    <button type="button" class="btn btn-info" @click="send" :disabled="start && time > 0">
        {{ text }}
    </button>
</template>

<script type="text/ecmascript-6">

  export default {
  	props: {
        second: {
            type: Number,
            default: 60
        }
    },
    data() {
        return {
            time: 0,
            start: false
        }
    },
    methods: {
        send() {
            this.$emit('send')
            if(this.start ){
                this.time = this.second
                 this.timer()
            }
        },
        timer() {
            if (this.time > 0) {
                this.time = this.time - 1
                setTimeout(this.timer, 1000)
            }
        }
    },
    computed: {
        text() {
            if(this.start && this.time > 0){
                return ' 重新获取(' + this.time + 's)';
            }else{
                this.start = false;
                return '获取验证码';
            }
        }
    }
  };
</script>

<style scoped>

</style>
