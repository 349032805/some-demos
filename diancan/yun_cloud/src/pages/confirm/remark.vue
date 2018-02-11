<template>
    <div class="remark-unit">
        <div class="remark-theme" @click="switchRemark()">
            <div class="remark-title">备注</div>
            <div class="content overflow" v-show="displayRemark==false">{{remarkAllStr}}</div>
            <div class="float-right">
                <img src="../../assets/display.png" class="display" :class="{'convert':displayRemark == true}">
            </div>
        </div>

        <div class="remark-detail" v-show="displayRemark == true">
            <div class="split-line">
                <div class="top-line"></div>
            </div>
            <div class="taste-options">
                <ul class="clearfix">
                    <li v-for="(item,index) in demandList" :key="index">
                        <button type="button" @click="demand(item,index)" :class="{'current-select':item.flag == true}">{{item.fsaskname}}</button>
                    </li>
                </ul>
            </div>

            <div class="other-demand">
                <input type="text" placeholder="点击输入菜品要求" maxlength="50" v-model="remarkInputResult" class="text-input">
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'remark',
    props: {
        demandList: Array,
        selectDemandList: Array,
        remarkInput: String
    },
    data() {
        return {
            displayRemark: true,
            remarkAllStr: '',
            remarkArr: [],
            remarkInputResult: this.remarkInput,
            selectDemandListResult: this.selectDemandList
        }
    },
    watch: {
        remarkInputResult(val) {
            this.$emit('update:remarkInput', val)
        }
    },
    methods: {
        switchRemark() {
            //将需求输入的字符放入数组
            //判断依据:1.非空,2.不存在目标数组
            if (this.remarkInputResult) {
                let isExist = false;
                this.remarkArr.forEach((item, index) => {
                    if (item == this.remarkInputResult) {
                        isExist = true;
                    }
                });
                if (!isExist) {
                    this.remarkArr.push(this.remarkInputResult);
                }
            } else {
                this.remarkArr = [];
                this.demandList.forEach(item => {
                    if (item.flag) {
                        this.remarkArr.push(item.fsaskname);
                    }
                });
            }
            this.remarkAllStr = this.remarkArr.join(",");
            this.displayRemark = !this.displayRemark;
            console.log("折起选择的备注:" + this.remarkAllStr);
        },
        demand(option, index) {
            this.demandList[index].flag = !this.demandList[index].flag;
            this.$emit('update:selectDemandList', this.demandList)


            if (option.flag) {
                this.remarkArr.push(option.fsaskname);
            } else {
                this.remarkArr.forEach((item, index) => {
                    if (option.fsaskname == item) {
                        this.remarkArr.splice(index, 1);
                    }
                });
            }
            this.remarkAllStr = this.remarkArr.join(",");
            console.log("选择的备注:" + this.remarkAllStr);
        }
    }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.remark-unit {
    background: #fff;

    .remark-theme {
        height: .88rem;
        line-height: .88rem;
        padding: 0 .3rem;
        font-size: .32rem;
        background: #fff;
        margin-top: .2rem;

        .remark-title {
            float: left;
            width: 15%;
        }
        .content {
            float: left;
            width: 80%;
            height: 100%;
            text-align: right;
            color: #999;
        }

        .display {
            width: 13px;
            height: 8px;
            vertical-align: middle;
            -webkit-transition: all .4s ease 0s;
            -moz-transition: all .4s ease 0s;
            -o-transition: all .4s ease 0s;
            transition: all .4s ease 0s;
        }

        .convert {
            -webkit-transform: rotate(-180deg);
            -moz-transform: rotate(-180deg);
            -ms-transform: rotate(-180deg);
            transform: rotate(-180deg);
            animation-fill-mode: forwards;
        }
    }

    .remark-detail {
        padding: .2rem .3rem;
        background: #fff;
        position: relative;

        .split-line {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            margin: auto;
            width: 92%;
            height: 1px;
        }

        .taste-options {
            color: #333;
            font-size: .26rem;

            ul {
                height: .54rem;
                li {
                    float: left;
                    margin-right: .2rem;
                    margin-bottom: .2rem;

                    button {
                        border: 1px solid #c7c7c7;
                        border-radius: 4px;
                        padding: 0 .2rem;
                        height: .64rem;
                        background: #fff;
                        font-size: .26rem;
                        min-width: 1.68rem;
                        ;
                    }
                    .current-select {
                        background: rgba(255, 94, 79, 0.10);
                        border: 1px solid #FF5E4F;
                        color: #FF5E4F;
                    }
                }
            }

            .mt10 {
                margin-top: .2rem;
            }
        }

        .other-demand {
            margin-top: .2rem;
            background: #fff;
        }
    }
}
</style>
