<template>

<div>
	<ul class = 'actname'>
		<li v-for = '(item,index) in actlist' :key="index" v-show='index <= 1 ' :class='{isshow:isshow}'>
		<span class='title' :style='{background:colorFilter(item.logoNameCode)}'>{{item.logoName}}</span>
		{{item.activityName}}</li>
	</ul>
	<div class="numact">
           <span data-content='▼' @click.stop='show()' ref="tarGet" v-show='actlist.length>2'>{{actlist.length}}个活动</span>
    </div>
	
</div>
	

</template>

<script>
export default{
	name : "activity",
	props:{
		actlist:Array
	},
     data(){
     	return {
     		isshow:false
     	}
     },
	methods : {
         colorFilter(status) {
            let _color = '';
            if (status == '1') {
                _color = 'green';
            }
            if (status == '2' || status == '3' || status == '4') {
                _color = 'orange';
            }
            if (status == '5' || status == '6') {
                _color = 'red'
            }
            return _color;
        },
        show(){
        	
        	if (this.actlist.length>2) {
        		if(this.$refs.tarGet.dataset.content=='▼'){this.$refs.tarGet.dataset.content='▲'}
        		else{this.$refs.tarGet.dataset.content='▼'}

        		this.isshow=!this.isshow
        	}

        }
     },



}
	
</script>

<style scoped lang="less">
.actname{
font-family: PingFangSC-Regular;
font-size: 0.24rem;
color: #666666;
letter-spacing: 0;
display: inline-block;
width: 70%;
}	

.isshow{display:block !important}

.numact {
    display: inline-block;
    float: right;
    span:after{
    	content:attr(data-content);
    	background: #FFFFFF; 
    };
}

.title{
        font-size:.2rem;
        padding:.04rem;
        margin-right:.12rem;
    }
</style>