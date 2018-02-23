<template>
  <div class="test2">
    <div class="title">程序员老黄历</div>
    <div class="date">{{todayStr}}</div>
    <div class="good board">
      <div class="theme">
        <table>
          <tr><td>宜</td></tr>
        </table>
      </div>
      <div class="content">
        <ul>
          <li v-for="(item,index) in goodList" :key="index">
            <div class="do">{{item.name}}</div>
            <div class="description">{{item.good}}</div>
          </li>

        </ul>
      </div>
    </div>

     <div class="bad board">
      <div class="theme">
        <table>
          <tr><td>不宜</td></tr>
        </table>
      </div>
      <div class="content">
        <ul>
          <li v-for="(item,index) in badList" :key="index">
            <div class="do">{{item.name}}</div>
            <div class="description">{{item.bad}}</div>
          </li>
        </ul>
      </div>
    </div>

    	<div class="line-tip">
				<strong>座位朝向：</strong>面向<span class="direction-value">{{direction}}</span>写程序，bug最少。
			</div>
			<div class="line-tip">
				<strong>今日宜饮：</strong><span class="drink-value">{{drinkStr}}</span>
			</div>
      <!-- ★★☆☆☆ -->
			<div class="line-tip">
				<strong>女神亲近指数：</strong><span class="goddes-value">{{starStr}}</span>
			</div>
			
  </div>
</template>

<script type="text/ecmascript-6">
import allKeys from "@/utils/key";

export default {
  data() {
    return {
      iday: 0,
      specials: allKeys.specials,
      todayStr: "",
      goodList: [],
      badList: [],
      direction: "",
      drinkStr: "",
      starStr: ""
    };
  },
  created() {
    this.initProphesy();
  },
  methods: {
    random(dayseed, indexseed) {
      let n = dayseed % 11117;
      for (let i = 0; i < 100 + indexseed; i++) {
        n = n * n;
        n = n % 11117; // 11117 是个质数
      }
      return n;
    },
    initProphesy() {
      let today = new Date();
      this.iday =
        today.getFullYear() * 10000 +
        (today.getMonth() + 1) * 100 +
        today.getDate();

      let weeks = ["日", "一", "二", "三", "四", "五", "六"];
      let directions = allKeys.directions;
      let activities = allKeys.activities;
      let tools = allKeys.tools;
      let varNames = allKeys.varNames;
      let drinks = allKeys.drinks;

      this.todayStr =
        "今天是" +
        today.getFullYear() +
        "年" +
        (today.getMonth() + 1) +
        "月" +
        today.getDate() +
        "日 星期" +
        weeks[today.getDay()];

      this.direction =
        directions[this.random(this.iday, 2) % directions.length];
      this.drinkStr = this.pickRandom(drinks, 2).join("，");
      this.starStr = this.star(this.random(this.iday, 6) % 5 + 1);
      this.pickTodaysLuck();
    },
    star(num) {
      let result = "";
      let i = 0;
      while (i < num) {
        result += "★";
        i++;
      }
      while (i < 5) {
        result += "☆";
        i++;
      }
      return result;
    },
    pickTodaysLuck() {
      // 生成今日运势
      let _activities = this.filter(allKeys.activities);

      let numGood = this.random(this.iday, 98) % 3 + 2;
      let numBad = this.random(this.iday, 87) % 3 + 2;
      let eventArr = this.pickRandomActivity(_activities, numGood + numBad);

      let specialSize = this.pickSpecials();
      // console.log(specialSize)

      for (let i = 0; i < numGood; i++) {
        this.goodList.push(eventArr[i]);
      }

      console.log("-------------------------------------");
      console.log(this.goodList);
      console.log("-------------------------------------");

      for (let i = 0; i < numBad; i++) {
        this.badList.push(eventArr[numGood + i]);
      }

      console.log("-------------------------------------");
      console.log(this.badList);
      console.log("-------------------------------------");
    },
    filter(activities) {
      // 去掉一些不合今日的事件
      let result = [];
      // 周末的话，只留下 weekend = true 的事件
      if (this.isWeekend()) {
        for (let i = 0; i < activities.length; i++) {
          if (activities[i].weekend) {
            result.push(activities[i]);
          }
        }
        return result;
      }

      return activities;
    },
    isWeekend() {
      return new Date().getDay() == 0 || new Date().getDay() == 6;
    },
    pickSpecials() {
      // 添加预定义事件
      let specialSize = [0, 0];
      for (let i = 0; i < this.specials.length; i++) {
        let special = this.specials[i];

        if (this.iday == special.date) {
          if (special.type == "good") {
            specialSize[0]++;
            this.goodList.push({
              name: special.name,
              good: special.description
            });
          } else {
            specialSize[1]++;
            this.badList.push({
              name: special.name,
              bad: special.description
            });
          }
        }
      }
      return specialSize;
    },
    pickRandomActivity(activities, size) {
      // 从 activities 中随机挑选 size 个
      let picked_events = this.pickRandom(activities, size);
      for (let i = 0; i < picked_events.length; i++) {
        picked_events[i] = this.parse(picked_events[i]);
      }
      return picked_events;
    },
    pickRandom(array, size) {
      // 从数组中随机挑选 size 个
      let result = [];
      for (let i = 0; i < array.length; i++) {
        result.push(array[i]);
      }

      for (let j = 0; j < array.length - size; j++) {
        let index = this.random(this.iday, j) % result.length;
        result.splice(index, 1);
      }

      return result;
    },

    parse(event) {
      // 解析占位符并替换成随机内容
      let result = { name: event.name, good: event.good, bad: event.bad }; // clone

      if (result.name.indexOf("%v") != -1) {
        result.name = result.name.replace(
          "%v",
          varNames[this.random(this.iday, 12) % varNames.length]
        );
      }

      if (result.name.indexOf("%t") != -1) {
        result.name = result.name.replace(
          "%t",
          tools[this.random(this.iday, 11) % tools.length]
        );
      }

      if (result.name.indexOf("%l") != -1) {
        result.name = result.name.replace(
          "%l",
          (this.random(this.iday, 12) % 247 + 30).toString()
        );
      }
      return result;
    }
  }
};
</script>

<style lang="less">
.test2 {
  width: 320px;
  margin: 50px auto;
  .title {
    color: #bbb;
    font-weight: bold;
    background: #555;
    text-align: center;
    line-height: 30px;
  }

  .date {
    font-size: 16px;
    font-weight: bold;
    line-height: 40px;
    text-align: center;
  }

  .good {
    background: #ffffaa;
    .theme {
      background: #ffee44;
    }
  }

  .board {
    position: relative;
    padding-bottom: 1px;
    padding-top: 3px;
    clear: both;
    .theme {
      float: left;
      width: 100px;
      text-align: center;
      font-size: 40px;
      position: absolute;
      top: 0;
      bottom: 0;

      table {
        position: absolute;
        width: 100%;
        height: 100%;
        border: none;

        td {
          vertical-align: middle;
          font-weight: bold;
          font-size: 40px;
        }
      }
    }
    .content {
      margin-left: 115px;
      padding-right: 10px;
      padding-top: 1px;

      ul {
        li {
          line-height: 150%;

          .do {
            font-weight: bold;
            color: #666;
            font-size: 20px;
            line-height: 30px;
          }

          .description {
            font-size: 15px;
            color: #777;
            line-height: 20px;
            margin-bottom: 10px;
          }
        }
      }
    }
  }

  .bad {
    margin-top: 5px;
    background: #ffddd3;
    .theme {
      background: #ff4444;
      color: #fff;
    }
  }

  .line-tip {
    font-size: 15px;
    margin-top: 13px;
    padding-left: 10px;

    strong {
      font-weight: bold;
    }

    .direction-value {
      color: #4a4;
      font-weight: bold;
    }

    .goddes-value {
      color: #f87;
    }
  }
}
</style>
