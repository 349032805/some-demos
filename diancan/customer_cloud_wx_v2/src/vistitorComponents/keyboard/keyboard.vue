<template>
    <div class="keyboard">
        <div class="key-number">
            <div class="opt-box clearfix">
                <div @click="cancel()" class="number text no-left no-bottom">关闭</div>
                <div class="number  no-left no-bottom"></div>
                <div @click="confirm()" class="number text no-left no-bottom">完成</div>
            </div>
            <div class="num-box clearfix">
                <div @click="keynum('1')" class="number no-left">1</div>
                <div @click="keynum('2')" class="number">2</div>
                <div @click="keynum('3')" class="number">3</div>
                <div @click="keynum('4')" class="number no-left">4</div>
                <div @click="keynum('5')" class="number">5</div>
                <div @click="keynum('6')" class="number">6</div>
                <div @click="keynum('7')" class="number no-left">7</div>
                <div @click="keynum('8')" class="number">8</div>
                <div @click="keynum('9')" class="number">9</div>
                <div class="number bg-gray no-left no-bottom"></div>
                <div @click="keynum('0')" class="number no-bottom">0</div>
                <div @click="keydelete()" class="number bg-gray no-bottom"><img style="width:20px;margin-top:16px;" src="../../assets/delete-icon.png"></div>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        data(){
            return {
                inputVal:'',
            }
        },
        methods : {
            keynum(number){
                if(this.inputVal.length>=3) return;
                this.inputVal += number;
                console.log(this.inputVal)
            },
            keydelete(){
                if(this.inputVal){
                    this.inputVal = this.inputVal.slice(0,-1);
                }
            },
            confirm(){
                this.$emit('confirmKeyboard',this.inputVal);
                this.inputVal = '';
            },
            cancel(){
                this.$emit('closeKeyboard');
                this.inputVal = '';
            }
        }
    }
</script>
<style lang="less">
    .keyboard{
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        // background:rgba(195,199,207,0.30);
        background: #EBECF1;
        z-index:1000;
        .opt-box{
            border-bottom:1px solid #e8e8e8;
            border-top:1px solid #e8e8e8;
            background: #fafafa;
        }
        .key-number{
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            .number{
                float:left;
                width:33.33333333%;
                height: 54px;
                border-left:1px solid #8c8c8c;
                border-bottom:1px solid #8c8c8c;
                box-sizing:border-box;
                text-align: center;
                line-height: 54px;
                font-size: 28px;
                background: #fff;
            }
            .text{
                font-size:18px;
            }
            .no-left{
                border-left: none;
            }
            .no-bottom{
                border-bottom: none;
            }
            .bg-gray{
                // background: #EBECF1;
                background: rgba(195,199,207,0.30);
            }
        }
    }
</style>