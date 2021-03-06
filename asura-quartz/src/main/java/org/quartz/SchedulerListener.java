
/* 
 * Copyright 2001-2009 Terracotta, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package org.quartz;

/**
 * <p>
 * The interface to be implemented by classes that want to be informed of major
 * <code>{@link org.quartz.Scheduler}</code> events.
 * </p>
 * 
 * @see org.quartz.Scheduler
 * @see org.quartz.JobListener
 * @see org.quartz.TriggerListener
 * 
 * @author James House
 */
public interface SchedulerListener {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * is scheduled.
     * </p>
     */
    void jobScheduled(Trigger trigger);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * is unscheduled.
     * </p>
     */
    void jobUnscheduled(String triggerName, String triggerGroup);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code>
     * has reached the condition in which it will never fire again.
     * </p>
     */
    void triggerFinalized(Trigger trigger);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code>
     * or group of <code>{@link org.quartz.Trigger}s</code> has been paused.
     * </p>
     * 
     * <p>
     * If a group was paused, then the <code>triggerName</code> parameter
     * will be null.
     * </p>
     */
    void triggersPaused(String triggerName, String triggerGroup);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code>
     * or group of <code>{@link org.quartz.Trigger}s</code> has been un-paused.
     * </p>
     * 
     * <p>
     * If a group was resumed, then the <code>triggerName</code> parameter
     * will be null.
     * </p>
     */
    void triggersResumed(String triggerName, String triggerGroup);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been added.
     * </p>
     */
    void jobAdded(JobDetail jobDetail);
    
    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * has been deleted.
     * </p>
     */
    void jobDeleted(String jobName, String groupName);
    
    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * or group of <code>{@link JobDetail}s</code> has been
     * paused.
     * </p>
     * 
     * <p>
     * If a group was paused, then the <code>jobName</code> parameter will be
     * null. If all jobs were paused, then both parameters will be null.
     * </p>
     */
    void jobsPaused(String jobName, String jobGroup);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link JobDetail}</code>
     * or group of <code>{@link JobDetail}s</code> has been
     * un-paused.
     * </p>
     * 
     * <p>
     * If a group was resumed, then the <code>jobName</code> parameter will
     * be null. If all jobs were paused, then both parameters will be null.
     * </p>
     */
    void jobsResumed(String jobName, String jobGroup);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a serious error has
     * occurred within the scheduler - such as repeated failures in the <code>JobStore</code>,
     * or the inability to instantiate a <code>Job</code> instance when its
     * <code>Trigger</code> has fired.
     * </p>
     * 
     * <p>
     * The <code>getErrorCode()</code> method of the given SchedulerException
     * can be used to determine more specific information about the type of
     * error that was encountered.
     * </p>
     */
    void schedulerError(String msg, SchedulerException cause);

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> to inform the listener
     * that it has move to standby mode.
     * </p>
     */
    void schedulerInStandbyMode();

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> to inform the listener
     * that it has started.
     * </p>
     */
    void schedulerStarted();
    
    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> to inform the listener
     * that it has shutdown.
     * </p>
     */
    void schedulerShutdown();
    
    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> to inform the listener
     * that it has begun the shutdown process.
     * </p>
     */
    void schedulerShuttingdown();
}
