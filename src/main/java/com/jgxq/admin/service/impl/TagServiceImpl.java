package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.entity.Tag;
import com.jgxq.admin.mapper.TagMapper;
import com.jgxq.admin.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.define.TagType;
import com.jgxq.common.res.PlayerBasicRes;
import com.jgxq.common.res.TagSearchRes;
import com.jgxq.common.res.TeamBasicRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-21
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TeamServiceImpl teamService;

    @Autowired
    private PlayerServiceImpl playerService;

    public List<TagSearchRes> getTagList(Integer newsId){
        QueryWrapper<Tag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("news_id",newsId);
        List<Tag> tags = tagMapper.selectList(tagQuery);

        Map<Integer, List<Tag>> map = tags.stream().collect(Collectors.groupingBy(tag -> tag.getObjectType().intValue()));
        List<TagSearchRes> tagRes = new ArrayList<>();
        List<Tag> teams = map.get(TagType.TEAM.getValue());
        List<Tag> players = map.get(TagType.PLAYER.getValue());
        if(teams!=null){
            List<Integer> teamIds = teams.stream().map(Tag::getObjectId).collect(Collectors.toList());
            List<TeamBasicRes> teamList = teamService.getBasicTeamsByIds(teamIds);
            List<TagSearchRes> teamTags = teamList.stream().map(t -> {
                TagSearchRes tag = new TagSearchRes();
                tag.setType(TagType.TEAM.getValue());
                tag.setObjectId(t.getId());
                tag.setLogo(t.getLogo());
                tag.setName(t.getName());
                return tag;
            }).collect(Collectors.toList());
            tagRes.addAll(teamTags);
        }
        if(players!=null){
            List<Integer> playerIds = players.stream().map(Tag::getObjectId).collect(Collectors.toList());
            List<PlayerBasicRes> playerList = playerService.getBasicByIds(playerIds);
            List<TagSearchRes> playerTags = playerList.stream().map(p -> {
                TagSearchRes tag = new TagSearchRes();
                tag.setType(TagType.PLAYER.getValue());
                tag.setObjectId(p.getId());
                tag.setLogo(p.getHeadImage());
                tag.setName(p.getName());
                return tag;
            }).collect(Collectors.toList());
            tagRes.addAll(playerTags);
        }
        return tagRes;
    }

    @Override
    public List<TagSearchRes> searchTag(String keyword) {
        //使用Sql做搜索
        return tagMapper.searchTag(keyword);
    }

    public List<TagSearchRes> searchTagByEs(String keyword) {
        //TODO : 使用elasticsearch搜索
        return null;
    }
}