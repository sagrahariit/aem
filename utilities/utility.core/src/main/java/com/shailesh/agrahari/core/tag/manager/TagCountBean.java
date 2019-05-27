package com.shailesh.agrahari.core.tag.manager;

import com.day.cq.tagging.Tag;

public class TagCountBean
{
  private Tag tag;
  private int usageCount;
   
  public TagCountBean(Tag tag, int usageCount)
  {
    setTag(tag);
    setUsageCount(usageCount);
  }
   
  public Tag getTag()
  {
    return this.tag;
  }
   
  public void setTag(Tag tag)
  {
    this.tag = tag;
  }
   
  public int getUsageCount()
  {
    return this.usageCount;
  }
   
  public void setUsageCount(int usageCount)
  {
    this.usageCount = usageCount;
  }
}
