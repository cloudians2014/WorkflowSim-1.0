/**
 *  Copyright 2007-2008 University Of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.workflowsim.clusering;

import org.workflowsim.Job;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Weiwei Chen
 */
public class HorizontalClustering extends BasicClustering{
    
    private int clusterNum;
    private int clusterSize;
    private Map mDepth2Task;

    public HorizontalClustering(int clusterNum, int clusterSize){
        super();
        this.clusterNum = clusterNum;
        this.clusterSize = clusterSize;
        this.mDepth2Task = new HashMap<Integer, Map>();

    }
    
    @Override
    public void run()
 
    {
        if(clusterNum >0 || clusterSize>0){
            for(Iterator it = getTaskList().iterator();it.hasNext();)
            {
                Task task = (Task)it.next();
                int depth = task.getDepth();
                if(!mDepth2Task.containsKey(depth)){
                    mDepth2Task.put(depth, new ArrayList<Task>());
                }
                ArrayList list = (ArrayList)mDepth2Task.get(depth);
                if (!list.contains(task)){
                    list.add(task);
                }

            }
        }
        if(clusterNum > 0){
            bundleClustering();
        }else if(clusterSize >0){
            collapseClustering();
        }

        updateDependencies();
        addClustDelay();
    }

    
    private void bundleClustering(){
        
//        for (Iterator it = mDepth2Task.entrySet().iterator();it.hasNext();){
//            Map.Entry pairs = (Map.Entry<Integer, ArrayList>)it.next();
//            ArrayList list = (ArrayList)pairs.getValue();
//            int num = list.size();
//            int avg = num / this.clusterNum ;
//            if(avg * this.clusterNum < num) 
//                avg ++;
//            if (avg <=0 )avg = 1;
//            
//            for(int i = 0; i < this.clusterNum; i ++){
//                int start = i * avg;
//                int end = start + avg - 1;
//                if (end >= num)
//                    end = num - 1;
//                if(end < start)
//                    break;
//                Job job = addTasks2Job(list.subList(start, end + 1));
//            }
//            
//
//        }
        for (Iterator it = mDepth2Task.entrySet().iterator();it.hasNext();){
            Map.Entry pairs = (Map.Entry<Integer, ArrayList>)it.next();
            ArrayList list = (ArrayList)pairs.getValue();
            
            long seed = System.nanoTime();
            Collections.shuffle(list, new Random(seed));
            seed = System.nanoTime();
            Collections.shuffle(list, new Random(seed));
            
            int num = list.size();
            int avg_a = num / this.clusterNum ;
            int avg_b = avg_a;
            if(avg_a * this.clusterNum < num) {
                avg_b ++;
            }
            
            int mid = num - this.clusterNum * avg_a;
            if (avg_a <=0 )avg_a = 1;
            if (avg_b <= 0)avg_b = 1;
            int start = 0, end = -1;
            for(int i = 0; i < this.clusterNum; i ++){
                start = end + 1;
                if(i < mid){
                    //use avg_b
                    end = start + avg_b - 1;
                }else{
                    //use avg_a
                    end = start + avg_a - 1;
                    
                }
                
                
                if (end >= num)
                    end = num - 1;
                if(end < start)
                    break;
                
                addTasks2Job(list.subList(start, end + 1));
            }
            

        }
    }
    private void collapseClustering(){
        for (Iterator it = mDepth2Task.entrySet().iterator();it.hasNext();){
            Map.Entry pairs = (Map.Entry<Integer, ArrayList>)it.next();
            ArrayList list = (ArrayList)pairs.getValue();
            
            long seed = System.nanoTime();
            Collections.shuffle(list, new Random(seed));
            seed = System.nanoTime();
            Collections.shuffle(list, new Random(seed));
            
            int num = list.size();
            int avg = this.clusterSize ;

            int start = 0;
            int end = 0;
            int i = 0;
            do{
                start = i * avg;
                end = start + avg - 1;
                i ++ ;
                if (end >= num) end = num - 1;
                Job job = addTasks2Job(list.subList(start, end + 1));
            }while(end < num - 1);

        }
    }
    
}
