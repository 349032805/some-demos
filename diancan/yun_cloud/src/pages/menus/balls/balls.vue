<template>
    <div>
        <div class="ball-container">
            <div v-for="ball in balls">
                <transition
                    name="drop"
                    v-on:before-enter="beforeEnter"
                    v-on:enter="enter"
                    v-on:after-enter="afterEnter"
                    tag="div">
                    <div v-show="ball.show" class="ball">
                    <div class="inner inner-hook"></div>
                    </div>
                </transition>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                dropBalls:[],
                balls:[
                    {
                    show:false
                    },
                    {
                    show:false
                    },
                    {
                    show:false
                    },
                    {
                    show:false
                    },
                    {
                    show:false
                    }
                ]
            }
        },
        methods: {
            //小球动画开始
            drop(e){
                this.$nextTick(()=>{
                    for(let i=0;i<this.balls.length;i++){
                        let ball =this.balls[i]
                        if(!ball.show){
                            ball.show=true;
                            ball.el=e.target;
                            this.dropBalls.push(ball);
                            return;
                        }
                    }
                })
            },
            beforeEnter:function(el){
                let count = this.balls.length;
                while(count--){
                    let ball =this.balls[count];
                    if(ball.show){
                        let rect = ball.el.getBoundingClientRect();
                        let x = rect.left-32;
                        let y = -(window.innerHeight-rect.top-22)
                        el.style.display='';
                        el.style.webkitTransform = `translate3d(0,${y}px,0)`
                        el.style.transform = `translate3d(0,${y}px,0)`
                        let inner = el.querySelector('.inner-hook')
                        inner.style.webkitTransform = `translate3d(${x}px,0,0)`
                        inner.style.transform = `translate3d(${x}px,0,0)`
                    }
                }
            },
            enter(el){
                let rf = el.offsetHeight;
                this.$nextTick(()=>{
                    el.style.webkitTransform = 'translate3d(0,0,0)'
                    el.style.transform = 'translate3d(0,0,0)'
                    let inner = el.querySelector('.inner-hook')
                    inner.style.webkitTransform = 'translate3d(0,0,0)'
                    inner.style.transform = 'translate3d(0,0,0)'
                });
            },
            afterEnter(el){
                let ball = this.dropBalls.shift();
                if(ball){
                    ball.show=false;
                    el.style.display='none'
                }
            }
        }
    }
</script>
<style lang="less">
    @import './balls.less';
</style>