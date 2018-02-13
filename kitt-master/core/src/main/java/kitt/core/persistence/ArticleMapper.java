
package kitt.core.persistence;


import kitt.core.domain.Adverpic;
import kitt.core.domain.Article;
import kitt.core.domain.HotNews;
import kitt.core.domain.Video;
import kitt.core.util.NewsConfig;
import kitt.core.util.PageQueryParam;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static kitt.ext.mybatis.Where.*;

/**
 * Created by lxj on 14/11/12.
 */
public interface ArticleMapper {

    /******************************  第一部分，行业资讯  *********************************/
    /******************************  第一部分，行业资讯  *********************************/
    /******************************  第一部分，行业资讯  *********************************/

    String $articles=" articles ";

    @Select("select * from articles where id=#{id} and isdelete=0")
    public Article $getById(int id);

    public default Article getById(int id){
        return id!=0 ? $getById(id):null;
    }

    //添加文章
    @Insert("insert into articles(title, summary, keywords, content, author, source, createtime, " +
            "parentid, path, pathname, lastupdateman, lastmodifytime, lastedittime, bannerurl,category) values(#{title}, #{summary}, #{keywords}, " +
            "#{content}, #{author}, #{source}, now(),  #{parentid}, #{path}, #{pathname}, #{lastupdateman}, " +
            "now(), #{lastedittime}, #{bannerurl},#{category})")
    @Options(useGeneratedKeys=true)
    public int $insertArticle(Article article);

    //更新文章
    @Update("update articles set title=#{title}, summary=#{summary}, keywords=#{keywords}," +
            " content=#{content}, author=#{author}, source=#{source}, path=#{path}, pathname=#{pathname}, " +
            "lastupdateman=#{lastupdateman}, lastmodifytime=now(), lastedittime=#{lastedittime}, bannerurl=#{bannerurl}" +
            " where id=#{id} and isdelete=0" )
    public int $updateArticle(Article article);

    @Update("update articles, (select count(*)>1 as cnt from articles where parentid=#{id} and isdelete=0) as cc set articles.haschild=cnt where id=#{id}")
    void $updateHasChildByIdDelete(int id);

    @Update("update articles set haschild=1  where id=#{id} and haschild=0")
    void $updateHasChildByIdInsert(int id);

    @Transactional
    public default int saveArticle(Article article){
        if(article.getId() != null){
            Article articleInDb = $getById(article.getId());
            if(articleInDb != null) {
                return $updateArticle(article);
            }
            return 0;
        } else{
            if(this.$getById(article.getParentid())!=null) {
                $updateHasChildByIdInsert(article.getParentid());
            }
            return $insertArticle(article);
        }
    }

    public default Map<String, Object> getAncestorsById(int id){
        if(id == 0) return null;
        Map<String, Object> map = new HashMap<>();
        List<Article> list = new ArrayList<Article>();
        Article article = $getById(id);
        while (article != null) {
            list.add(article);
            if (article.getParentid() != 0) article = $getById(article.getParentid());
            else break;
        }
        map.put("list", list);
        map.put("parent", getById(id));
        return map;
    }

    public default Article getRootParentByParentId(int id) {
        if (id == 0) return  null;
        Article article = $getById(id);
        if (article == null) return null;
        while ($getById(article.getParentid()) != null) {
            article = $getById(article.getParentid());
        }
        return article;
    }

    String $whereArticleByParentAndContent= where+"parentid in <foreach collection='parentids' index='i' item='parentid' open='(' separator=',' close=')'>#{parentid}</foreach> and isdelete=false <if test='content!=null'> and title like #{content}</if>"+$where;
    String $orderbycreatetimedesc = "order by sequence asc, lastedittime desc";
    String $orderbylastupdatetimedesc="order by lastedittime desc";
    @Select(script+ selectCountAllFrom +$articles+$whereArticleByParentAndContent+$script)
    public int $countArticleByParentidAndContent(@Param("parentids")List<Integer> parentid, @Param("content")String content);

    @Select(script+" select *, if(focusname like '%首页%', true, false) as homepage, " +
            " if(focusname like '%热点1%', true, false) as hot01, " +
            " if(focusname like '%热点2%', true, false) as hot02" +
            " from "+$articles+$whereArticleByParentAndContent + $orderbylastupdatetimedesc + limitAndOffset+$script)
    public List<Map<String, Object>> $listArticleByParentidAndContent(@Param("parentids") List<Integer> parentid, @Param("content")String content, @Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Map<String, Object>> pageArticleByParentidAndContent(int parentid, String content, int page, int pagesize){
        return new Pager(
                $countArticleByParentidAndContent(Arrays.asList(parentid), $like$(content)),
                (int limit, int offset)->$listArticleByParentidAndContent(Arrays.asList(parentid), $like$(content), limit, offset),
                page,
                pagesize);
    }

    public default Pager<Map<String, Object>> siteArticleByParentidAndContent(int parentid, String content, int page, int pagesize){
        Article article=this.getById(parentid);
        List<Integer> parentIds= new ArrayList<>();
        if(article.getParentid()==0){
            //找二级目录
            List<Integer> ids=findParentId(parentid);
            if(ids.size()==0){
                //如果没有二级目录
                parentIds.add(parentid);
            } else{
                parentIds.addAll(ids);
            }
        } else{
            parentIds.add(parentid);
        }
        return new Pager(
                $countArticleByParentidAndContent(parentIds, $like$(content)),
                (int limit, int offset)->$listArticleByParentidAndContent(parentIds, $like$(content), limit, offset),
                page,
                pagesize);
    }
    //删除文章
    @Update("update articles set isdelete=1, lastupdateman=#{lastupdateman}, lastmodifytime=now() where id=#{id}")
    int deleteArticleById(@Param("id")int id,
                          @Param("lastupdateman")String lastupdateman);

    //置顶/取消置顶文章
    @Update("update articles set stickied=Mod((stickied+1),2) where id=#{id}")
    boolean toSticked(@Param("id")int id);


    @Update("update articles set  lastedittime=now() where id=#{id}")
    boolean toaltertime(@Param("id")int id);

    //查看置顶的文章个数
    @Select(" select count(*) from articles where isdelete=0 and stickied=1 ")
    public int countStick();


    default int deleteById(int id, String lastupdateman){
        Article article=getById(id);
        if(article!=null && article.getParentid() != 0)
            $updateHasChildByIdDelete(article.getParentid());
        return deleteArticleById(id, lastupdateman);
    }

    @Select("select count(1) from articles where parentid=#{id} and isdelete=0")
    int getArticleCountByParentid(@Param("id")int id);
    //根据parentid查询文章 没有limit
    @Select("select * from articles where parentid=#{id} and isdelete=0 order by sequence asc, lastmodifytime desc")
    public List<Article> getArticleListByParentidNoLimit(@Param("id")int id);

    //根据parentid查询文章
    @Select("select * from articles where parentid=#{id} and isdelete=0 order by sequence asc, lastmodifytime desc limit #{limit} offset #{offset}")
    public List<Article> getArticleListByParentid(@Param("id")int id,
                                                  @Param("limit")int limit,
                                                  @Param("offset")int offset);

    //通过path查找Article
    @Select("select * from articles where path=#{path} and isdelete=0 limit 1")
    public Article getArticleByPath(String path);

    @Select("select * from articles where pathname=#{pathname} and isdelete=0 limit 1")
    public Article getArticleByPathname(String pathname);

    //通过path, parentid查找Article
    @Select("select * from articles where path=#{path} and parentid=#{parentid} and isdelete=0")
    public Article getArticleByPathParentid(@Param("path")String path,
                                            @Param("parentid")Integer parentid);

    //根据path查询id
    @Select("select id from articles where path=#{path} and isdelete=0")
    public Integer getIdByPath(String path);

    //根据id 查找孙子文章列表
    @Select("<script>" +
            " select a1.id, a1.title as title, a1.bannerurl as bannerurl, a2.title as ptitle from articles a1 inner join articles a2 on a1.parentid=a2.id where a1.parentid in (select id from articles where parentid = #{id}) and a1.isdelete=0 group by a1.id order by a1.lastmodifytime desc" +
            " <if test='limit != 0'> limit #{limit} </if>" +
            "</script>")
    List<Map<String, Object>> getGrandSonArticlesById(@Param("id")int id,
                                                      @Param("limit")int limit);

    default List<Map<String, Object>> getSpecialGrandSonArticlesById(int id, int limit, List<String> sonMenuList){
        List<Map<String, Object>> mapList = getGrandSonArticlesById(id, 0);
        List<Map<String, Object>> articleList = new ArrayList<>();
        int num = 0;
        for(Map<String, Object> map : mapList){
            if(sonMenuList.contains(map.get("ptitle").toString())){
                articleList.add(map);
                num++;
            }
            if(num == limit) break;
        }
        return articleList;
    }


    public default List<Article> getSeveralArticlesByParentPath(String path, int limit){
        Integer id = getIdByPath(path);
        if(id != null) return getArticleListByParentid(id, limit, 0);
        return null;
    }

    //改变固顶级别
    @Update("update articles set sequence=#{sequence}, lastmodifytime=now() where id=#{id}")
    public boolean changeSequence(@Param("sequence")int sequence,
                                  @Param("id")int id);

    @Update("update articles set viewtimes=viewtimes+1 where id=#{id} and isdelete=0")
    void doAddViewTimes(int id);

    default void doAddViewTimesById(int id){
        Article article=getById(id);
        if(article != null) {
            do {
                doAddViewTimes(id);
                id = getById(id).getParentid();
            } while(id != 0);
        }
    }


    //设置 或者 取消 文章为焦点文章
    @Update("update articles set focus=#{focus}, focusname=#{focusname}, lastmodifytime=now() where id=#{id}")
    int setArticleFocus(@Param("id")int id,
                        @Param("focus")int focus,
                        @Param("focusname")String focusname);

    //删除或者取消删除焦点文章
    @Update("update hotnews set isdelete=#{isdelete} where aid=#{aid} and isdelete != #{isdelete}")
    int setOrCancelHotNewsDelete(@Param("aid")int aid,
                                 @Param("isdelete") int isdelete);

    //设置文章的banner图片
    @Update("update articles set bannerurl=#{bannerurl}, lastmodifytime=now() where id=#{id}")
    boolean setArticleBannerUrlById(@Param("id")int id,
                                    @Param("bannerurl")String bannerurl);

    @Select("select * from articles where pathname=#{pathname} and parentid=#{id}")
    Article getArticleByPathnameParentid(@Param("pathname")String pathname,
                                         @Param("id")Integer id);

    @Select("select id from articles where isdelete=0 and (title=#{title1} or title=#{title2}) ")
    List<Integer> getIdListByTitle(@Param("title1")String title1,
                                   @Param("title2")String title2);

    /**
     * 根据Id查询第一个字文章的path
     * @param id
     * @return
     */
    @Select("select * from articles where parentid=#{id} and isdelete=0 order by sequence limit 1")
    Article getFirstSonArticleById(Integer id);


    @Select("select * from articles where parentid=(select id from articles where isdelete=0 and parentid=#{id} order by sequence limit 1) and isdelete=0 limit 1")
    Article getFirstGrandSonArticleById(Integer id);

    /**************************  第二部分，热点资讯  ******************************/
    /**************************  第二部分，热点资讯  ******************************/
    /**************************  第二部分，热点资讯  ******************************/

    //向hotnews表插入数据
    @Insert("insert into hotnews(aid) values(#{aid})")
    int insertHotNews(HotNews hotNews);

    //查询焦点文章
    @Select("select * from hotnews where aid=#{aid} limit 1")
    HotNews getHotNewsByAid(int aid);

    @Select("select * from hotnews where id=#{id} limit 1")
    HotNews getHotNewsById(int id);

    //设置 或者 取消 焦点文章
    default int addOrCancelHotNews(HotNews hotNews, int focus){
        if(focus != 0) {
            if (getHotNewsByAid(hotNews.getAid()) == null) return insertHotNews(hotNews);
            setOrCancelHotNewsDelete(hotNews.getAid(), 0);
            return 1;
        } else{
            return setOrCancelHotNewsDelete(hotNews.getAid(), 1);
        }
    }

    @Select("select count(1) from hotnews h inner join articles a on h.aid=a.id where h.isdelete=0 and a.isdelete=0 ")
    public int getHotNewsCount();

    @Select("select a.id as aid, a.title as title, a.author as author, a.focus as focus, a.viewtimes as viewtimes, " +
            " a.lastedittime as lastedittime, a.bannerurl as bannerurl, h.id as id, h.level as level, " +
            " if(a.focusname like '%首页%', true, false) as homepage, " +
            " if(focusname like '%热点1%', true, false) as hot01, " +
            " if(focusname like '%热点2%', true, false) as hot02, " +
            " if(a.parentid in (select id from articles where parentid=#{id1} or parentid=#{id2}), false, true) as showhomecheck," +
            " h.sequence as sequence, h.isshow as isshow from hotnews h inner join articles a on h.aid=a.id " +
            " where h.isdelete=0 and a.isdelete=0 " +
            " order by isshow desc, sequence limit #{limit} offset #{offset}")
    public List<Map<String, Object>> getHotNewsList(@Param("id1") int id1,
                                                    @Param("id2") int id2,
                                                    @Param("limit") int limit,
                                                    @Param("offset") int offset);

    public default Pager<Map<String, Object>> pageHotNews(int id1, int id2, int page, int pagesize){
        return Pager.config(this.getHotNewsCount(), (int limit, int offset) -> this.getHotNewsList(id1, id2, limit, offset))
                .page(page, pagesize);
    }

    //更改hotnews的类别
    @Update("update hotnews set level=#{level} where id=#{id}")
    boolean doChangeHotNewsLevelMethod(@Param("id")int id,
                                       @Param("level")int level);

    //更改hotnews的顺序
    @Update("update hotnews set sequence=#{sequence} where id=#{id}")
    boolean doChangeHotNewsSequenceMethod(@Param("id")int id,
                                          @Param("sequence")int sequence);

    //更改article的lastedittile

    //site 查询热点资讯列表
    @Select("select a.id as aid, a.title as title, a.author as author, a.viewtimes as viewtimes, " +
            " a.lastedittime as lastedittime, a.bannerurl as bannerurl, a.summary as summary, h.id as id, " +
            " h.level as level, h.sequence as sequence from hotnews h left join articles a on h.aid=a.id " +
            " where h.isdelete=0 and h.isshow=1 and (a.focus=#{focus1} or a.focus=#{focus2}) order by level, sequence limit #{limit} offset #{offset}")
    public List<Map<String, Object>> getHotNewsListSite(@Param("focus1")int focus1,
                                                        @Param("focus2")int focus2,
                                                        @Param("limit")int limit,
                                                        @Param("offset")int offset);


    //site
    @Select("select a.id as aid, a.title as title, a.author as author, a.viewtimes as viewtimes, " +
            " a.lastedittime as lastedittime, a.bannerurl as bannerurl, a.summary as summary, h.id as id, " +
            " h.level as level, h.sequence as sequence from hotnews h left join articles a on h.aid=a.id " +
            " where h.isdelete=0 and a.isdelete=0 and h.isshow=1 and a.focusname like #{focusname} order by sequence, lastmodifytime desc limit #{limit} offset #{offset}")
    public List<Map<String, Object>> getHotNewesListByFocusname(@Param("focusname")String name,
                                                                @Param("limit")int limit,
                                                                @Param("offset")int offset);

    @Select("select a.id as id, a.title as title,a.bannerurl as bannerurl, a.lastedittime as lastedittime, b.title as ptitle, if(b.parentid = 0, b.title, c.title) as roottitle  from hotnews h " +
            " left join articles a  on h.aid=a.id " +
            " left join articles b on a.parentid=b.id " +
            " left join articles c on b.parentid=c.id " +
            " where h.isdelete=0 and a.isdelete=0 and h.isshow=1 and a.focusname like #{focusname} order by h.sequence, a.lastmodifytime desc limit #{limit} offset #{offset}")
    public List<Map<String, Object>> getHotNewsByFocusname(@Param("focusname")String name,
                                                           @Param("limit")int limit,
                                                           @Param("offset")int offset);

    //删除热点资讯
    @Update("update hotnews set isdelete=1 where id=#{id}")
    int doDeleteHotNewMethod(int id);

    //设置热点资讯是否在前台显示
    @Update("update hotnews set isshow=(isshow+1)%2 where id=#{id}")
    int doSetCancelHotNewsShowMethod(int id);

    /************************  第三部分，宣传视频  ***************************/
    /************************  第三部分，宣传视频  ***************************/
    /************************  第三部分，宣传视频  ***************************/

    //添加宣传视频
    @Insert("insert into videos(name, url, length, picurl, remarks, createtime, lastedittime, lasteditman) " +
            " values(#{name}, #{url}, #{length}, #{picurl}, #{remarks}, now(), now(), #{lasteditman})")
    @Options(useGeneratedKeys=true)
    int doAddArticleVideoMethod(Video video);

    //修改宣传视频
    @Update("update videos set name=#{name}, url=#{url}, length=#{length}, picurl=#{picurl}, remarks=#{remarks}, lastedittime=now(), lasteditman=#{lasteditman} where id=#{id}")
    boolean doUpdateArticleVideoMethod(Video video);

    @Select("<script>" +
            "select count(1) from videos " +
            "<where>" +
            "<if test='content!=\"\" and content!=null'> name like #{content}</if>" +
            " and isdelete=0" +
            "</where>" +
            "</script>")
    public int countArticleVideosByContent(@Param("content")String content);

    @Select("<script>" +
            "select * from videos " +
            "<where>" +
            "<if test='content!=\"\" and content!=null'> name like #{content}</if>" +
            " and isdelete=0" +
            "</where>" +
            " order by isshow desc, sequence, lastupdatetime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Video> getArticleVideosByContent(@Param("content")String content,
                                                 @Param("limit") int limit,
                                                 @Param("offset") int offset);

    public default Pager<Video> pageArticleVideoByContent(String content, int page, int pagesize){
        return Pager.config(this.countArticleVideosByContent($like$(content)), (int limit, int offset) -> this.getArticleVideosByContent($like$(content), limit, offset))
                .page(page, pagesize);
    }

    //根据id查询 Video
    @Select("select * from videos where id=#{id}")
    Video getVideoById(int id);

    //设置 或者 取消 视频在前台显示
    @Update("update videos set isshow=(isshow+1)%2 where id=#{id}")
    int setShowCancelVideoMethod(int id);

    //改变视频的顺序
    @Update("update videos set sequence=#{sequence} where id=#{id}")
    boolean doChangeVideoSequenceMethod(@Param("id")int id,
                                        @Param("sequence")int sequence);

    //添加视频备注
    @Update("update videos set remarks=#{remarks} where id=#{id}")
    int doAddArticleRemarksMethod(@Param("id")int id,
                                  @Param("remarks")String remarks);

    //删除视频
    @Update("update videos set isdelete=1 where id=#{id} and isdelete=0")
    int doDeleteArticleVideoMethod(int id);

    @Select("select * from videos where isdelete=0 and isshow=1 order by sequence, lastedittime desc")
    List<Video> getArtivleVideoList();

    @Update("update videos set viewtimes=viewtimes+1 where id=#{id}")
    int doAddVideoViewTimesMethod(int id);




    //-------lich  site  前台咨询
    //查询热点文章
    @Select("select a.`title`,a.`bannerurl`,a.`id` from articles a,hotnews h where a.id=h.aid and h.isdelete=0 and h.isshow=1 and a.isdelete=0 order by h.sequence, h.id  desc limit #{limit}")
    public List<Article>  findhotNews(@Param("limit")int limit);

    //查询行业要闻,国际煤炭,企业动态,政策追踪
    @Select("select a1.*  from articles a1 " +
            " where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`haschild`=1 and a.`parentid`=0)" +
            "  and a1.isdelete=0 order by a1.sequence,a1.`createtime` desc limit #{limit}")
    public List<Article>  findIndustryNews(@Param("title") String title,@Param("limit")int limit);

    //查询宏观经济,相关行业,独家视点,行业数据
    @Select("select  a2.* from articles a2 where a2.parentid=" +
            "   (select a1.id  from articles a1 where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`parentid`=0)" +
            "    and a1.title=#{subtitle} and a1.isdelete=0)" +
            "    and a2.isdelete=0 order by a2.sequence, a2.createtime desc limit #{limit}")
    public List<Article> findList(@Param("title") String title,@Param("subtitle") String subtitle,@Param("limit")int limit);




    //行业数据--煤炭数据
//    @Select( "select a2.*  from articles  a2 where a2.parentid in "+
//            "(select a1.id from articles a1 where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`parentid`=0) and a1.isdelete=0) and a2.isdelete=0 order by a2.sequence,a2.createtime desc limit #{limit}")
//    public List<Article> findCoalList(@Param("title") String title,@Param("limit")int limit);

    @Select("select a3.category, a2.* ,a3.path as pPath from articles  a2 , (select * from articles a1 where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`parentid`=0) and a1.isdelete=0) a3  where a2.parentid =a3.id"+
            " and a2.isdelete=0 order by a2.sequence,a2.createtime desc limit #{limit}")
    public List<Article> findCoalList(@Param("title") String title,@Param("limit")int limit);

    @Select("select count(*)  from articles a1 " +
            "   where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`haschild`=1 and a.`parentid`=0)" +
            "   and a1.isdelete=0 order by a1.sequence,a1.`createtime` desc ")
    public int countArticle(@Param("title") String title);

    @Select("select a1.*  from articles a1 " +
            "   where a1.parentid=(select a.id as id from  articles a  where a.title=#{title} and a.isdelete=0 and a.`haschild`=1 and a.`parentid`=0)" +
            "  and a1.isdelete=0 order by a1.sequence,a1.`createtime` desc limit #{limit} offset #{offset}")
    public List<Article> findArticleByTitle(@Param("title")String title,@Param("limit") int limit, @Param("offset") int offset);
    public default Pager<Article> pageArticleByTitle(String title, int page, int pagesize){
        return Pager.config(this.countArticle(title),(int limit, int offset)->this.findArticleByTitle(title, limit, offset)).page(page, pagesize);
    }


    //-----lich  广告图片
    //添加广告图片
    @Insert("insert into adverpic(picurl,createtime,linkurl,lastedittime, lasteditman,comment)  values(#{picurl},#{createtime},#{linkurl},#{lastedittime},#{lasteditman},#{comment})")
    @Options(useGeneratedKeys=true)
    public int addAderverPic(Adverpic adverpic);

    //修改广告图片
    @Update("update adverpic set picurl=#{picurl}, linkurl=#{linkurl}, comment=#{comment}, lasteditman=#{lasteditman},lastedittime=#{lastedittime} where id=#{id}")
    public int updateAderverPic(Adverpic adverpic);


    //获取广告图片总数
    @Select("select count(*) from adverpic where isdelete=0 ")
    public int countArticleAdverpic();
    //获取广告图片列表
    @Select("select * from adverpic where isdelete=0" +
            " order by sequence, lastedittime desc limit #{limit} offset #{offset}")
    public List<Adverpic> getArticleAdverpicList(@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Adverpic> getArticleAdverpic(int page, int pagesize){
        return Pager.config(this.countArticleAdverpic(), (int limit, int offset) -> this.getArticleAdverpicList(limit, offset))
                .page(page, pagesize);
    }


    //设置 或者 取消 广告图片在前台展示
    @Update("update adverpic set isshow=(isshow+1)%2 where id=#{id}")
    int setShowCancelAdverMethod(int id);


    //改变广告图片的顺序
    @Update("update adverpic set sequence=#{sequence} where id=#{id}")
    boolean doChangeAdverpicSequenceMethod(@Param("id")int id,
                                           @Param("sequence")int sequence);


    //删除广告图片
    @Update("update adverpic set isdelete=1 where id=#{id}")
    boolean doDeleteArticleAdverMethod(int id);

    //获取四张广告图片

    @Select( "select * from adverpic where isdelete=0 and isshow=1 order by sequence, lastedittime desc limit #{limit} ")
    public List<Adverpic> findPartAdverPic(@Param("limit")int limit);


    //行情资讯第一级标题path查找
    @Select( "select path from articles a where a.title=#{title} and a.isdelete=0  and a.`parentid`=0 ")
    public String findPathByTitle(@Param("title")String limit);

    // 行情资讯第二级标题path查找
    @Select("select path from articles a1 where a1.parentid=(select a.id as id from  articles a  where a.title=#{title1} and a.isdelete=0 and a.`parentid`=0) and a1.isdelete=0 and a1.title=#{title2}")
    public String  findPathBySubTitle(@Param("title1")String title1,@Param("title2")String title2);

    //通过category查找Article
    @Select("select * from articles where category=#{category} and isdelete=0  limit 1")
    public Article findArticleByCategory(String path);


    @Select("select * from articles where parentid=#{value} and haschild=1")
    public List<Integer> findParentId(int id);



    /***************************************site模块行情资讯改版******************************************/
    @Select("  select a1.*  from articles a1,articles a2 where a1.parentid=a2.id and a2.title=#{title} and a2.isdelete=0 and a1.isdelete=0" +
            "  and a1.haschild=0 and a2.haschild=1  order by a1.lastedittime desc limit #{indexnum},#{pageSize} ")
    public List<Article> findArticlesByTitle(@Param("title")String title,@Param("indexnum")int indexnum,@Param("pageSize")int pageSize);

    @Select("  select count(*)  from articles a1,articles a2 where a1.parentid=a2.id and a2.title=#{title} and a2.isdelete=0 and a1.isdelete=0" +
            "  and a1.haschild=0 and a2.haschild=1 ")
    public int countArticlesByTitle(@Param("title")String title);

    @Select("select * from ( (select a1.* from articles a1,articles " +
            " a2 where a1.isdelete=0 and a2.isdelete=0  and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0 " +
            " and a2.title in ('行业要闻','国际煤炭','企业动态','政策追踪','易煤快讯','易煤动态','第三方服务','互联网＋','网站公告')) union (select " +
            " a1.* from articles a1,articles a2,articles a3 where a1.isdelete=0  and a2.isdelete=0 and a3.isdelete=0 " +
            " and a3.title in ('宏观经济','相关行业','独家视点') and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            ") ) bb order by stickied desc,lastedittime desc limit #{indexnum},#{pageSize}" )
    public List<Article> findAllArticles(@Param("indexnum")int  indexnum,@Param("pageSize")int pageSize);


    @Select("  select * from (" +
            "  (select a1.*  from articles a1,articles a2 where a1.isdelete=0 and a2.isdelete=0 and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0" +
            "   and a2.title in ('行业要闻','国际煤炭','企业动态','政策追踪','易煤快讯','易煤动态','第三方服务','互联网＋','网站公告'))" +
            "   union" +
            "   (select a1.*  from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in ('宏观经济','相关行业','独家视点') and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            "   ) " +
            "  ) bb order by lastedittime desc" +
            "  limit #{indexnum},#{pageSize}")
    public List<Article> getNewestArticles(@Param("indexnum")int  indexnum,@Param("pageSize")int pageSize);

    @Select("select count(*) from ((select a1.* from articles a1 where isdelete=0 and haschild=0 and stickied=1 " +
            "order by lastedittime desc ) union (select * from ( (select a1.* from articles a1,articles " +
            "a2 where a1.isdelete=0 and a2.isdelete=0 and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0 " +
            "and a2.title in ('行业要闻','国际煤炭','企业动态','政策追踪','易煤快讯','易煤动态','第三方服务','互联网＋','网站公告')) union (select " +
            "a1.* from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 " +
            "and a3.title in ('宏观经济','相关行业','独家视点') and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            ") ) bb order by lastedittime desc )) tt ")
    public int countAllArticles();



    @Select(" select count(*) from ((select a1.* from articles a1 where isdelete=0 and haschild=0 and stickied=1 ) " +
            " union " +
            " (" +
            " select bb.* from (" +
            "  (select a1.* from articles a1,articles a2 where a1.isdelete=0 " +
            "and a2.isdelete=0 and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0 and a2.title in " +
            "('行业要闻','国际煤炭','企业动态','政策追踪','易煤快讯','易煤动态','第三方服务','互联网＋','网站公告' )) " +
            "union " +
            "(select a1.* from articles a1,articles a2,articles " +
            "a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in ('宏观经济','相关行业','独家视点') " +
            "and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 ) " +
            ") bb " +
            ")  ) gg")
    public int countArticlesNum();
    //相关行业
    @Select("select a1.*  from articles a1,articles a2 where a1.isdelete=0 and a2.isdelete=0 and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0" +
            " and a2.title in ('行业要闻','国际煤炭','企业动态','政策追踪')" +
            " union" +
            " select a1.*  from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in ('宏观经济','相关行业') and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            " order by lastedittime desc limit #{indexnum},#{pageSize}")
    public List<Article> findByTitle(@Param("indexnum")int  indexnum,@Param("pageSize")int pageSize);


    @Select("select count(*) from (" +
            "select a1.*  from articles a1,articles a2 where a1.isdelete=0 and a2.isdelete=0 and a1.parentid=a2.id and a2.haschild=1 and a1.haschild=0" +
            "  and a2.title in ('行业要闻','国际煤炭','企业动态','政策追踪')" +
            "  union" +
            "  select a1.*  from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in ('宏观经济','相关行业') and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            ") b")
    public int countByTitle();


    @Select("<script>"+
            " select a1.*  from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in <foreach collection='str' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" > #{item}</foreach> and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            " order by lastedittime desc limit #{indexnum},#{pageSize} </script>")
    public List<Article> getArticlesByTitle(@Param("indexnum")int  indexnum,@Param("pageSize")int pageSize,@Param("str")String[] str1);



    @Select("<script>"+
            " select count(*)  from articles a1,articles a2,articles a3 where a1.isdelete=0 and a2.isdelete=0 and a3.isdelete=0 and a3.title in <foreach collection='str' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" > #{item}</foreach> and a2.parentid=a3.id and a1.parentid=a2.id and a3.haschild=1 " +
            " </script>")
    public int countArticByTitle(@Param("str")String[] str1);


    @Select(" select *  from articles  where  isdelete=0  and id!=#{id} and  parentid=#{parentid} order by lastedittime desc,viewtimes limit 0,12 ")
    public List<Article> getRelatedArticles(@Param("id")int id,@Param("parentid")int parentid);

   @Select("select a1.*  from articles a1,articles a2 ,articles a3 where a2.parentid=a3.id and a1.parentid=a2.id and a3.title=#{title1} and a3.isdelete=0 and a2.isdelete=0 and a1.isdelete=0 " +
           "and a2.title=#{title2} order by a1.lastedittime desc limit 5 ")
    public List<Article> findArtics(@Param("title1")String title1,@Param("title2")String title2);

    @Select("select * from articles where pathname=#{pathname} and isdelete=0 and haschild=1")
    Article getNoChilcArticleByPathname(String pathname);

    @Select("select count(1) from articles where parentid in ${contentIDSQL} and haschild=0 and isdelete=0")
    int countAllTypeNew(@Param("contentIDSQL") String contentIDSQL);

    @Select("<script>" +
            " select a.*, if(b.parentid=0, b.title, c.title) as rootParentTitle from articles a left join articles b " +
            " on a.parentid=b.id left join articles c on b.parentid=c.id" +
            " where a.parentid in ${contentIDSQL} and a.isdelete=0 and a.haschild=0 order by a.lastmodifytime desc " +
            " <choose><when test='anchor==null or anchor==\"\"'>limit #{pageQuery.pagesize} offset #{pageQuery.indexNum}</when><otherwise>limit ${pageQuery.rowNum}  offset 0</otherwise></choose>" +
            "</script>")
    List<Map<String, Object>> getAllTypeLatestNews(@Param("pageQuery") PageQueryParam param,
                                                   @Param("anchor") String scrtop,
                                                   @Param("contentIDSQL") String contentIDSQL);

    public default PageQueryParam getAllTypeNewsList(PageQueryParam param, String scrtop) {
        String contentIdSQL = " ( ";
        for (int i=0; i<NewsConfig.getNewsContentList().size(); i++) {
            Article article = getNoChilcArticleByPathname(NewsConfig.getNewsContentList().get(i));
            if (article != null) {
                if (i != 0) contentIdSQL += ", ";
                contentIdSQL += article.getId();
            }
        }
        contentIdSQL += " ) ";
        int totalCount = countAllTypeNew(contentIdSQL);
        List<Map<String, Object>> articleList = getAllTypeLatestNews(param, scrtop, contentIdSQL);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(articleList);
        //如果有锚点,加载完数据，告诉前台当前是第几页
        if (org.apache.commons.lang3.StringUtils.isNotBlank(scrtop)) {
            param.setPage(param.getIndexNum() / param.getPagesize());
        }
        return param;
    }



}
