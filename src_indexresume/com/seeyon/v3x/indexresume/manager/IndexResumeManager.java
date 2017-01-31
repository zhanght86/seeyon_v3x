package com.seeyon.v3x.indexresume.manager;

import com.seeyon.v3x.indexresume.domain.IndexResumeInfo;

public interface IndexResumeManager {
void resumeStar(IndexResumeInfo info);
public void setStopFlag(boolean stopFlag) ;
void resume(int appType,String starDate,String endDate);
}
