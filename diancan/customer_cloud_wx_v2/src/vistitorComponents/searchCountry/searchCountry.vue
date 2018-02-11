<template>
    <div class='city-search_box'>
        <index-list :navs='navs' :navStyle='navStyle'>
            <index-sections :indexStyle='indexStyle' v-for="(item,index) in cityData" :key='index' :index="item.firstLetter">
                <template v-if='item.type == "square"'>
                    <div class="square-container">
                        <li class='square-box' v-for='(content,index) in item.cityOutVoList' :key='index' @click='chooseCity(content.fsareaname)'>{{content.fsareaname}}</li>
                    </div>
                </template>
                <template v-else>
                    <li class='long-box' v-for='content in item.cityOutVoList' @click='chooseCity(content.fsareaname)'>
                        <span>{{content.fsareaname}}</span>
                    </li>
                </template>
            </index-sections>
        </index-list>
    </div>
</template>

<script>
import { mapState } from 'vuex'
import IndexList from '@/components/index-list/index.vue';
import IndexSections from '@/components/index-sections/index.vue';
export default {
    created() {
        this.$indicator.open('正在加载城市列表...')
        this.$http('get', `${this.baseUrl}/getCityList`, {}).then(res => {
            if (res.code == '200') {
                let currentCity = {}
                currentCity.fsareaname = this.currentCity
                this.hotCity[0].cityOutVoList.push(currentCity)
                this.cityData = [...this.hotCity, ...res.model]
                this.navs = ['当前', '热门', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z']
                this.$nextTick(() => {
                    this.$indicator.close()
                })
            }
        })
    },
    activated() {
        this.$indicator.open('正在加载城市列表...')
        this.$nextTick(() => {
            this.$indicator.close()
        })
    },
    computed: {
        ...mapState([
            'CITY'
        ]),
        currentCity() {
            if (this.CITY == "") {
                return '上海市'
            } else {
                return this.CITY
            }
        }
    },
    components: {
        IndexList,
        IndexSections
    },
    methods: {
        chooseCity(city) {
            this.$store.commit('SAVE_CITY', city)
            sessionStorage.CITY = city
            this.$parent.cityShow = false
        },
    },
    data() {
        return {
            hotCity: [
                {
                    type: 'square',
                    firstLetter: '当前城市',
                    cityOutVoList: [
                    ]
                },
                {
                    type: 'square',
                    firstLetter: "热门城市",
                    cityOutVoList: [
                        {
                            fsareaname: "北京市"
                        },
                        {
                            fsareaname: "上海市"
                        },
                        {
                            fsareaname: "深圳市"
                        },
                        {
                            fsareaname: "东莞市"
                        },
                        {
                            fsareaname: "广州市",
                        },
                        {
                            fsareaname: "南京市",
                        },
                        {
                            fsareaname: "苏州市"
                        },
                        {
                            fsareaname: "杭州市"
                        },
                        {
                            fsareaname: "重庆市"
                        }
                    ]
                },
            ],
            navs: [],
            cityData: [],
            navStyle: {
                fontSize: '.4rem',
                color: '#00B3A1',
                marginTop: ".1rem"
            },
            indexStyle: {
                background: "#EFEFF4",
                fontSize: '14px',
                color: '#888888'
            }

        }
    }
}
</script>


<style lang='less'>
.city-search_box {
    background: #EFEFF4;
    .square-container {
        padding-left: 15px;
        .square-box {
            margin-right: 5%;
            width: 25%;
            border-radius: 3px;
            font-size: 14px;
            background: #fff;
            text-align: center;
            padding: 7px 0;
            display: inline-block;
            &:nth-of-type(4),
            &:nth-of-type(7),
            &:nth-of-type(10) {
                margin-top: 12px;
            }
            &:active {
                background: #E6E6E6;
            }
        }
    }

    .long-box {
        background: #fff;
        padding-left: 30px;
        &:active {
            background: #FAFAFA;
        }
        >span {
            width: 100%;
            display: inline-block;
            border-bottom: 1px solid #EFEFF4;
            font-size: 16px;
            padding: 7px 0;
        }
    }
}
</style>


