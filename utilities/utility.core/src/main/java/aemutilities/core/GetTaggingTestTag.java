package aemutilities.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
 
public class GetTaggingTestTag
extends WCMUsePojo
{
protected static Logger log = LoggerFactory.getLogger(GetTaggingTestTag.class);
List<TagCountBean> tagList = null;
 
public void activate()
{
  ValueMap vm = getResource().getValueMap();
  if (vm.containsKey("cq:tags"))
  {
    TagManager tagManager = (TagManager)getResourceResolver().adaptTo(TagManager.class);
    String[] tagIDs = (String[])vm.get("cq:tags", java.lang.String[].class);
     
   
    if (tagIDs.length > 0)
    {
      log.info("Total tags-" + tagIDs.length);
      this.tagList = new ArrayList();
      for (String tagID : tagIDs)
      {
        Tag resolvedTag = tagManager.resolve(tagID);
        if (resolvedTag != null)
        {
          TagCountBean tagCountBean = new TagCountBean(resolvedTag, getTrueTagCount(resolvedTag, getResourceResolver()).intValue());
          this.tagList.add(tagCountBean);
        }
      }
    }
  }
}
 
public static Integer getTrueTagCount(Tag tag, ResourceResolver resolver)
{
  int trueTagCount = 0;
   
  LinkedHashMap<String, String> map = new LinkedHashMap();
   
 
  map.put("tagid", tag.getTagID());
  map.put("tagid.property", "cq:tags");
   
 
  PredicateGroup predicates = PredicateGroup.create(map);
  QueryBuilder builder = (QueryBuilder)resolver.adaptTo(QueryBuilder.class);
  Query query = builder.createQuery(predicates, (Session)resolver.adaptTo(Session.class));
  query.setHitsPerPage(9223372036854775807L);
  SearchResult result = query.getResult();
  if (!result.getHits().isEmpty()) {
    trueTagCount = result.getHits().size();
  }
  return Integer.valueOf(trueTagCount);
}
 
 
//This is the methood that data-sly-list="${hello.tagCountBean}" hooks into 
public List<TagCountBean> getTagCountBean()
{
  return this.tagList;
}
}