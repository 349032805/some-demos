<template>
    <div class="invoice-unit">
        <div class="invoice-all">
            <div class="invoice-switch">
                <p class="float-left">开具发票</p>
                <div class="float-right" style="padding-top:.12rem">
                    <mt-switch v-model="openInvoiceResult"></mt-switch>
                </div>
            </div>

            <!-- 填写发票信息 -->
            <div class="invoice-write top-line" v-show="openInvoiceResult">
                <div class="invoice-type">
                    <span style="margin-right: .6rem">抬头: </span>
                    <div @click="selectTitle()" style="margin-right: .6rem">
                        <div class="option" :class="{'type-tick': isComResult,'type-circle':!isComResult}"></div> &nbsp;
                        <span>公司发票</span>
                    </div>
                    <div @click="selectTitle2()">
                        <div class="option" :class="{'type-tick': isPersonResult,'type-circle':!isPersonResult}"></div> &nbsp;
                        <span>个人发票</span>
                    </div>
                </div>

                <div class="invoice-input" v-show="isComResult">
                    <input type="text" placeholder="点击输入发票抬头" maxlength="30" v-model="invoiceHeaderResult" class="text-input">
                    <input type="text" placeholder="点击输入纳税人识别号" maxlength="30" v-model="codeResult" class="text-input" style="margin-top:.16rem">
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'invoice',
    props: {
        openInvoice: Boolean,
        isCom: Boolean,
        isPerson: Boolean,
        invoiceHeader: String,
        code: String
    },
    data() {
        return {
            openInvoiceResult: this.openInvoice,
            isComResult: this.isCom,
            isPersonResult: this.isPerson,
            invoiceHeaderResult: this.invoiceHeader,
            codeResult: this.code
        }
    },
    watch: {
        openInvoiceResult(val) {
            this.$emit('update:openInvoice', val)
        },
        isComResult(val) {
            this.$emit('update:isCom', val)
        },
        isPersonResult(val) {
            this.$emit('update:isPerson', val)
        },
        invoiceHeaderResult(val) {
            this.$emit('update:invoiceHeader', val)
        },
        codeResult(val) {
            this.$emit('update:code', val)
        }
    },
    methods: {
        selectTitle() {
            this.isComResult = true;
            this.isPersonResult = false;
        },
        selectTitle2() {
            this.isPersonResult = true;
            this.isComResult = false;
        }
    }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.invoice-unit {
    background: #fff;

    .invoice-all {
        margin-top: .2rem;
        padding: 0 .3rem;

        .invoice-switch {
            width: 100%;
            height: .88rem;
            line-height: .88rem;
            font-size: .32rem;
            color: #333;
        }

        .invoice-write {
            padding-top: .3rem;
            background: white;

            .invoice-type {
                margin-bottom: .26rem;
                font-size: .32rem;
                color: #333;
                * {
                    display: inline-block;
                    vertical-align: middle;
                }

                .option {
                    width: .46rem;
                    height: .46rem;
                    vertical-align: middle;
                }
                .type-tick {
                    background: url("../../assets/success_icon.png") no-repeat;
                    background-size: 100% 100%;
                }

                .type-circle {
                    background: url("../../assets/circle.png") no-repeat;
                    background-size: 100% 100%;
                }
            }

            .invoice-input{
                padding-bottom: .2rem;
            }
        }
    }
}
</style>
