<template>
    <div>
        <p @click="aa=true">fdafdsafdas</p>
        <confirm :dialog.sync="aa" title="提示" content="周志祥是一个大帅哥周志祥是一个大帅哥周志祥是一个大帅哥" cancelText="取消" confirmText="确定" @confirm="confirm"></confirm>

    </div>
</template>

<script>
import Confirm from '@/components/confirm'
  export default {
     mounted () {
         this.aa = true
     },
     data () {
         return {
           aa : false
         }
     },
     components : {
       Confirm
     },
     methods : {
         confirm () {
            console.log(11111)
         }
     }
  }
</script>
