<template>
   <div>
     <x-dialog  :value.sync='value'  >
         <div class="confirm">
             <div class="comfirm_text">
                <p class="title">{{title}}</p>
                <p class="content" v-html="content"></p>
             </div>
             <div class="confirm_button_box">
               <button class="cancel_btn" @click="$emit('update:dialog', false)">{{cancelText}}</button>
               <button class="confirm_btn" @click="confirm">{{confirmText}}</button>
             </div>
         </div>
     </x-dialog>
   </div>
</template>

<script>
  import xDialog from '@/components/x-dialog'
  export default {
      components : {
        xDialog
      },
      props : {
          dialog : {
              type : Boolean,
              default : false
          },
          title : String,
          content : String,
          confirmText: String,
          cancelText: String,
      },
      data () {
        return {
            value : false
        }
      },
      methods : {
        confirm () {
          this.$emit('update:dialog', false)
          this.$emit('confirm')
        }
      },
      watch : {
          dialog (value) {
            console.log(value)
            this.value = value
          },
          value (value) {
              if(value === false){
                this.$emit('update:dialog', value)
              }
          }
      }
  }
</script>


<style lang="less">
   .confirm{
       .comfirm_text{
         padding:0 10%;
          .title{
            font-size: 18px;
            color: #000000;
            padding-top:10px;
            padding-bottom:10px;
          }
          .content{
            font-size: 15px;
            color: #888888;
            line-height: 20px;
            padding-bottom:10px;
          }
       }
       .confirm_button_box{
         border-top:1px solid #eeeeee;
         font-size: 0;
          >button{
            display: inline-block;
            width:50%;
            font-size: 18px;
            padding:10px 0;
            background:#fff;
          }
         .cancel_btn{
           border-right:1px solid #eeeeee;
         }
       }
   }
</style>
