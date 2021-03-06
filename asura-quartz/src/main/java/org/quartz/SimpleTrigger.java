
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

import java.util.Date;


/**
 * <p>
 * A concrete <code>{@link org.quartz.Trigger}</code> that is used to fire a <code>{@link JobDetail}</code>
 * at a given moment in time, and optionally repeated at a specified interval.
 * </p>
 * 
 * @see org.quartz.Trigger
 * @see org.quartz.CronTrigger
 * @see org.quartz.TriggerUtils
 * 
 * @author James House
 * @author contributions by Lieven Govaerts of Ebitec Nv, Belgium.
 */
public class SimpleTrigger extends Trigger {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constants.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Required for serialization support. Introduced in Quartz 1.6.1 to 
     * maintain compatibility after the introduction of hasAdditionalProperties
     * method. 
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -3735980074222850397L;

    /**
     * <p>
     * Instructs the <code>{@link org.quartz.Scheduler}</code> that upon a mis-fire
     * situation, the <code>{@link org.quartz.SimpleTrigger}</code> wants to be fired
     * now by <code>Scheduler</code>.
     * </p>
     * 
     * <p>
     * <i>NOTE:</i> This instruction should typically only be used for
     * 'one-shot' (non-repeating) Triggers. If it is used on a trigger with a
     * repeat count > 0 then it is equivalent to the instruction <code>{@link #MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT}
     * </code>.
     * </p>
     */
    public static final int MISFIRE_INSTRUCTION_FIRE_NOW = 1;

    /**
     * <p>
     * Instructs the <code>{@link org.quartz.Scheduler}</code> that upon a mis-fire
     * situation, the <code>{@link org.quartz.SimpleTrigger}</code> wants to be
     * re-scheduled to 'now' (even if the associated <code>{@link org.quartz.Calendar}</code>
     * excludes 'now') with the repeat count left as-is.  This does obey the
     * <code>Trigger</code> end-time however, so if 'now' is after the
     * end-time the <code>Trigger</code> will not fire again.
     * </p>
     * 
     * <p>
     * <i>NOTE:</i> Use of this instruction causes the trigger to 'forget'
     * the start-time and repeat-count that it was originally setup with (this
     * is only an issue if you for some reason wanted to be able to tell what
     * the original values were at some later time).
     * </p>
     */
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2;

    /**
     * <p>
     * Instructs the <code>{@link org.quartz.Scheduler}</code> that upon a mis-fire
     * situation, the <code>{@link org.quartz.SimpleTrigger}</code> wants to be
     * re-scheduled to 'now' (even if the associated <code>{@link org.quartz.Calendar}</code>
     * excludes 'now') with the repeat count set to what it would be, if it had
     * not missed any firings.  This does obey the <code>Trigger</code> end-time 
     * however, so if 'now' is after the end-time the <code>Trigger</code> will 
     * not fire again.
     * </p>
     * 
     * <p>
     * <i>NOTE:</i> Use of this instruction causes the trigger to 'forget'
     * the start-time and repeat-count that it was originally setup with.
     * Instead, the repeat count on the trigger will be changed to whatever
     * the remaining repeat count is (this is only an issue if you for some
     * reason wanted to be able to tell what the original values were at some
     * later time).
     * </p>
     * 
     * <p>
     * <i>NOTE:</i> This instruction could cause the <code>Trigger</code>
     * to go to the 'COMPLETE' state after firing 'now', if all the
     * repeat-fire-times where missed.
     * </p>
     */
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3;

    /**
     * <p>
     * Instructs the <code>{@link org.quartz.Scheduler}</code> that upon a mis-fire
     * situation, the <code>{@link org.quartz.SimpleTrigger}</code> wants to be
     * re-scheduled to the next scheduled time after 'now' - taking into
     * account any associated <code>{@link org.quartz.Calendar}</code>, and with the
     * repeat count set to what it would be, if it had not missed any firings.
     * </p>
     * 
     * <p>
     * <i>NOTE/WARNING:</i> This instruction could cause the <code>Trigger</code>
     * to go directly to the 'COMPLETE' state if all fire-times where missed.
     * </p>
     */
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4;

    /**
     * <p>
     * Instructs the <code>{@link org.quartz.Scheduler}</code> that upon a mis-fire
     * situation, the <code>{@link org.quartz.SimpleTrigger}</code> wants to be
     * re-scheduled to the next scheduled time after 'now' - taking into
     * account any associated <code>{@link org.quartz.Calendar}</code>, and with the
     * repeat count left unchanged.
     * </p>
     * 
     * <p>
     * <i>NOTE/WARNING:</i> This instruction could cause the <code>Trigger</code>
     * to go directly to the 'COMPLETE' state if the end-time of the trigger
     * has arrived.
     * </p>
     */
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5;

    /**
     * <p>
     * Used to indicate the 'repeat count' of the trigger is indefinite. Or in
     * other words, the trigger should repeat continually until the trigger's
     * ending timestamp.
     * </p>
     */
    public static final int REPEAT_INDEFINITELY = -1;

    private static final int YEAR_TO_GIVEUP_SCHEDULING_AT = 2299;
    
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Data members.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    
    private Date startTime = null;

    private Date endTime = null;

    private Date nextFireTime = null;

    private Date previousFireTime = null;

    private int repeatCount = 0;

    private long repeatInterval = 0;

    private int timesTriggered = 0;

    private boolean complete = false;

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constructors.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> with no settings.
     * </p>
     */
    public SimpleTrigger() {
        super();
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur immediately, and
     * not repeat.
     * </p>
     */
    public SimpleTrigger(String name) {
        this(name, (String)null);
    }
    
    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur immediately, and
     * not repeat.
     * </p>
     */
    public SimpleTrigger(String name, String group) {
        this(name, group, new Date(), null, 0, 0);
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur immediately, and
     * repeat at the the given interval the given number of times.
     * </p>
     */
    public SimpleTrigger(String name, int repeatCount, long repeatInterval) {
        this(name, null, repeatCount, repeatInterval);
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur immediately, and
     * repeat at the the given interval the given number of times.
     * </p>
     */
    public SimpleTrigger(String name, String group, int repeatCount,
            long repeatInterval) {
        this(name, group, new Date(), null, repeatCount, repeatInterval);
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur at the given time,
     * and not repeat.
     * </p>
     */
    public SimpleTrigger(String name, Date startTime) {
        this(name, null, startTime);
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur at the given time,
     * and not repeat.
     * </p>
     */
    public SimpleTrigger(String name, String group, Date startTime) {
        this(name, group, startTime, null, 0, 0);
    }
    
    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur at the given time,
     * and repeat at the the given interval the given number of times, or until
     * the given end time.
     * </p>
     * 
     * @param startTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to fire.
     * @param endTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to quit repeat firing.
     * @param repeatCount
     *          The number of times for the <code>Trigger</code> to repeat
     *          firing, use {@link #REPEAT_INDEFINITELY} for unlimited times.
     * @param repeatInterval
     *          The number of milliseconds to pause between the repeat firing.
     */
    public SimpleTrigger(String name, Date startTime,
            Date endTime, int repeatCount, long repeatInterval) {
        this(name, null, startTime, endTime, repeatCount, repeatInterval);
    }
    
    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur at the given time,
     * and repeat at the the given interval the given number of times, or until
     * the given end time.
     * </p>
     * 
     * @param startTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to fire.
     * @param endTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to quit repeat firing.
     * @param repeatCount
     *          The number of times for the <code>Trigger</code> to repeat
     *          firing, use {@link #REPEAT_INDEFINITELY} for unlimited times.
     * @param repeatInterval
     *          The number of milliseconds to pause between the repeat firing.
     */
    public SimpleTrigger(String name, String group, Date startTime,
            Date endTime, int repeatCount, long repeatInterval) {
        super(name, group);

        setStartTime(startTime);
        setEndTime(endTime);
        setRepeatCount(repeatCount);
        setRepeatInterval(repeatInterval);
    }

    /**
     * <p>
     * Create a <code>SimpleTrigger</code> that will occur at the given time,
     * fire the identified <code>Job</code> and repeat at the the given
     * interval the given number of times, or until the given end time.
     * </p>
     * 
     * @param startTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to fire.
     * @param endTime
     *          A <code>Date</code> set to the time for the <code>Trigger</code>
     *          to quit repeat firing.
     * @param repeatCount
     *          The number of times for the <code>Trigger</code> to repeat
     *          firing, use {@link #REPEAT_INDEFINITELY}for unlimitted times.
     * @param repeatInterval
     *          The number of milliseconds to pause between the repeat firing.
     */
    public SimpleTrigger(String name, String group, String jobName,
            String jobGroup, Date startTime, Date endTime, int repeatCount,
            long repeatInterval) {
        super(name, group, jobName, jobGroup);

        setStartTime(startTime);
        setEndTime(endTime);
        setRepeatCount(repeatCount);
        setRepeatInterval(repeatInterval);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Get the time at which the <code>SimpleTrigger</code> should occur.
     * </p>
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * <p>
     * Set the time at which the <code>SimpleTrigger</code> should occur.
     * </p>
     * 
     * @exception IllegalArgumentException
     *              if startTime is <code>null</code>.
     */
    public void setStartTime(Date startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        Date eTime = getEndTime();
        if (eTime != null && startTime != null && eTime.before(startTime)) {
            throw new IllegalArgumentException(
                "End time cannot be before start time");    
        }

        this.startTime = startTime;
    }

    /**
     * <p>
     * Get the time at which the <code>SimpleTrigger</code> should quit
     * repeating - even if repeastCount isn't yet satisfied.
     * </p>
     * 
     * @see #getFinalFireTime()
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * <p>
     * Set the time at which the <code>SimpleTrigger</code> should quit
     * repeating (and be automatically deleted).
     * </p>
     * 
     * @exception IllegalArgumentException
     *              if endTime is before start time.
     */
    public void setEndTime(Date endTime) {
        Date sTime = getStartTime();
        if (sTime != null && endTime != null && sTime.after(endTime)) {
            throw new IllegalArgumentException(
                    "End time cannot be before start time");
        }

        this.endTime = endTime;
    }

    /**
     * <p>
     * Get the the number of times the <code>SimpleTrigger</code> should
     * repeat, after which it will be automatically deleted.
     * </p>
     * 
     * @see #REPEAT_INDEFINITELY
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * <p>
     * Set the the number of time the <code>SimpleTrigger</code> should
     * repeat, after which it will be automatically deleted.
     * </p>
     * 
     * @see #REPEAT_INDEFINITELY
     * @exception IllegalArgumentException
     *              if repeatCount is < 0
     */
    public void setRepeatCount(int repeatCount) {
        if (repeatCount < 0 && repeatCount != REPEAT_INDEFINITELY) {
            throw new IllegalArgumentException(
                    "Repeat count must be >= 0, use the "
                            + "constant REPEAT_INDEFINITELY for infinite.");
        }

        this.repeatCount = repeatCount;
    }

    /**
     * <p>
     * Get the the time interval (in milliseconds) at which the <code>SimpleTrigger</code>
     * should repeat.
     * </p>
     */
    public long getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * <p>
     * Set the the time interval (in milliseconds) at which the <code>SimpleTrigger</code>
     * should repeat.
     * </p>
     * 
     * @exception IllegalArgumentException
     *              if repeatInterval is <= 0
     */
    public void setRepeatInterval(long repeatInterval) {
        if (repeatInterval < 0) {
            throw new IllegalArgumentException(
                    "Repeat interval must be >= 0");
        }

        this.repeatInterval = repeatInterval;
    }

    /**
     * <p>
     * Get the number of times the <code>SimpleTrigger</code> has already
     * fired.
     * </p>
     */
    public int getTimesTriggered() {
        return timesTriggered;
    }

    /**
     * <p>
     * Set the number of times the <code>SimpleTrigger</code> has already
     * fired.
     * </p>
     */
    public void setTimesTriggered(int timesTriggered) {
        this.timesTriggered = timesTriggered;
    }

    protected boolean validateMisfireInstruction(int misfireInstruction) {
        if (misfireInstruction < MISFIRE_INSTRUCTION_SMART_POLICY) {
            return false;
        }

        if (misfireInstruction > MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
            return false;
        }

        return true;
    }

    /**
     * <p>
     * Updates the <code>SimpleTrigger</code>'s state based on the
     * MISFIRE_INSTRUCTION_XXX that was selected when the <code>SimpleTrigger</code>
     * was created.
     * </p>
     * 
     * <p>
     * If the misfire instruction is set to MISFIRE_INSTRUCTION_SMART_POLICY,
     * then the following scheme will be used: <br>
     * <ul>
     * <li>If the Repeat Count is <code>0</code>, then the instruction will
     * be interpreted as <code>MISFIRE_INSTRUCTION_FIRE_NOW</code>.</li>
     * <li>If the Repeat Count is <code>REPEAT_INDEFINITELY</code>, then
     * the instruction will be interpreted as <code>MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT</code>.
     * <b>WARNING:</b> using MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT 
     * with a trigger that has a non-null end-time may cause the trigger to 
     * never fire again if the end-time arrived during the misfire time span. 
     * </li>
     * <li>If the Repeat Count is <code>&gt; 0</code>, then the instruction
     * will be interpreted as <code>MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT</code>.
     * </li>
     * </ul>
     * </p>
     */
    public void updateAfterMisfire(Calendar cal) {
        int instr = getMisfireInstruction();
        if (instr == Trigger.MISFIRE_INSTRUCTION_SMART_POLICY) {
            if (getRepeatCount() == 0) {
                instr = MISFIRE_INSTRUCTION_FIRE_NOW;
            } else if (getRepeatCount() == REPEAT_INDEFINITELY) {
                instr = MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT;
            } else {
                // if (getRepeatCount() > 0)
                instr = MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
            }
        } else if (instr == MISFIRE_INSTRUCTION_FIRE_NOW && getRepeatCount() != 0) {
            instr = MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;
        }

        if (instr == MISFIRE_INSTRUCTION_FIRE_NOW) {
            setNextFireTime(new Date());
        } else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
            Date newFireTime = getFireTimeAfter(new Date());
            while (newFireTime != null && cal != null
                    && !cal.isTimeIncluded(newFireTime.getTime())) {
                newFireTime = getFireTimeAfter(newFireTime);

                if(newFireTime == null)
                	break;
                
                //avoid infinite loop
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(newFireTime);
                if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                    newFireTime = null;
                }
            }
            setNextFireTime(newFireTime);
        } else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) {
            Date newFireTime = getFireTimeAfter(new Date());
            while (newFireTime != null && cal != null
                    && !cal.isTimeIncluded(newFireTime.getTime())) {
                newFireTime = getFireTimeAfter(newFireTime);

                if(newFireTime == null)
                	break;
                
                //avoid infinite loop
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(newFireTime);
                if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                    newFireTime = null;
                }
            }
            if (newFireTime != null) {
                int timesMissed = computeNumTimesFiredBetween(nextFireTime,
                        newFireTime);
                setTimesTriggered(getTimesTriggered() + timesMissed);
            }

            setNextFireTime(newFireTime);
        } else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) {
            Date newFireTime = new Date();
            if (repeatCount != 0 && repeatCount != REPEAT_INDEFINITELY) {
                setRepeatCount(getRepeatCount() - getTimesTriggered());
                setTimesTriggered(0);
            }
            
            if (getEndTime() != null && getEndTime().before(newFireTime)) {
                setNextFireTime(null); // We are past the end time
            } else {
                setStartTime(newFireTime);
                setNextFireTime(newFireTime);
            } 
        } else if (instr == MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) {
            Date newFireTime = new Date();

            int timesMissed = computeNumTimesFiredBetween(nextFireTime,
                    newFireTime);

            if (repeatCount != 0 && repeatCount != REPEAT_INDEFINITELY) {
                int remainingCount = getRepeatCount()
                        - (getTimesTriggered() + timesMissed);
                if (remainingCount <= 0) { 
                    remainingCount = 0;
                }
                setRepeatCount(remainingCount);
                setTimesTriggered(0);
            }

            if (getEndTime() != null && getEndTime().before(newFireTime)) {
                setNextFireTime(null); // We are past the end time
            } else {
                setStartTime(newFireTime);
                setNextFireTime(newFireTime);
            } 
        }

    }

    /**
     * <p>
     * Called when the <code>{@link org.quartz.Scheduler}</code> has decided to 'fire'
     * the trigger (execute the associated <code>Job</code>), in order to
     * give the <code>Trigger</code> a chance to update itself for its next
     * triggering (if any).
     * </p>
     * 
     * @see #executionComplete(JobExecutionContext, JobExecutionException)
     */
    public void triggered(Calendar calendar) {
        timesTriggered++;
        previousFireTime = nextFireTime;
        nextFireTime = getFireTimeAfter(nextFireTime);

        while (nextFireTime != null && calendar != null
                && !calendar.isTimeIncluded(nextFireTime.getTime())) {
        	
            nextFireTime = getFireTimeAfter(nextFireTime);

            if(nextFireTime == null)
            	break;
            
            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                nextFireTime = null;
            }
        }
    }

    /**
     *  
     * @see Trigger#updateWithNewCalendar(Calendar, long)
     */
    public void updateWithNewCalendar(Calendar calendar, long misfireThreshold)
    {
        nextFireTime = getFireTimeAfter(previousFireTime);

        if (nextFireTime == null || calendar == null) {
            return;
        }
        
        Date now = new Date();
        while (nextFireTime != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {

            nextFireTime = getFireTimeAfter(nextFireTime);

            if(nextFireTime == null)
            	break;
            
            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                nextFireTime = null;
            }

            if(nextFireTime != null && nextFireTime.before(now)) {
                long diff = now.getTime() - nextFireTime.getTime();
                if(diff >= misfireThreshold) {
                    nextFireTime = getFireTimeAfter(nextFireTime);
                }
            }
        }
    }

    /**
     * <p>
     * Called by the scheduler at the time a <code>Trigger</code> is first
     * added to the scheduler, in order to have the <code>Trigger</code>
     * compute its first fire time, based on any associated calendar.
     * </p>
     * 
     * <p>
     * After this method has been called, <code>getNextFireTime()</code>
     * should return a valid answer.
     * </p>
     * 
     * @return the first time at which the <code>Trigger</code> will be fired
     *         by the scheduler, which is also the same value <code>getNextFireTime()</code>
     *         will return (until after the first firing of the <code>Trigger</code>).
     *         </p>
     */
    public Date computeFirstFireTime(Calendar calendar) {
        nextFireTime = getStartTime();

        while (nextFireTime != null && calendar != null
                && !calendar.isTimeIncluded(nextFireTime.getTime())) {
            nextFireTime = getFireTimeAfter(nextFireTime);
            
            if(nextFireTime == null)
            	break;
            
            //avoid infinite loop
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(nextFireTime);
            if (c.get(java.util.Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
                return null;
            }
        }
        
        return nextFireTime;
    }

    /**
     * <p>
     * Called after the <code>{@link org.quartz.Scheduler}</code> has executed the
     * <code>{@link JobDetail}</code> associated with the <code>Trigger</code>
     * in order to get the final instruction code from the trigger.
     * </p>
     * 
     * @param context
     *          is the <code>JobExecutionContext</code> that was used by the
     *          <code>Job</code>'s<code>execute(xx)</code> method.
     * @param result
     *          is the <code>JobExecutionException</code> thrown by the
     *          <code>Job</code>, if any (may be null).
     * @return one of the Trigger.INSTRUCTION_XXX constants.
     * 
     * @see #INSTRUCTION_NOOP
     * @see #INSTRUCTION_RE_EXECUTE_JOB
     * @see #INSTRUCTION_DELETE_TRIGGER
     * @see #INSTRUCTION_SET_TRIGGER_COMPLETE
     * @see #triggered(org.quartz.Calendar)
     */
    public int executionComplete(JobExecutionContext context,
            JobExecutionException result) {
        if (result != null && result.refireImmediately()) {
            return INSTRUCTION_RE_EXECUTE_JOB;
        }

        if (result != null && result.unscheduleFiringTrigger()) {
            return INSTRUCTION_SET_TRIGGER_COMPLETE;
        }

        if (result != null && result.unscheduleAllTriggers()) {
            return INSTRUCTION_SET_ALL_JOB_TRIGGERS_COMPLETE;
        }

        if (!mayFireAgain()) {
            return INSTRUCTION_DELETE_TRIGGER;
        }

        return INSTRUCTION_NOOP;
    }

    /**
     * <p>
     * Returns the next time at which the <code>Trigger</code> is scheduled to fire. If
     * the trigger will not fire again, <code>null</code> will be returned.  Note that
     * the time returned can possibly be in the past, if the time that was computed
     * for the trigger to next fire has already arrived, but the scheduler has not yet
     * been able to fire the trigger (which would likely be due to lack of resources
     * e.g. threads).
     * </p>
     *
     * <p>The value returned is not guaranteed to be valid until after the <code>Trigger</code>
     * has been added to the scheduler.
     * </p>
     *
     * @see org.quartz.TriggerUtils#computeFireTimesBetween(org.quartz.Trigger, org.quartz.Calendar, java.util.Date, java.util.Date)
     */
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * <p>
     * Returns the previous time at which the <code>SimpleTrigger</code> 
     * fired. If the trigger has not yet fired, <code>null</code> will be
     * returned.
     */
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    /**
     * <p>
     * Set the next time at which the <code>SimpleTrigger</code> should fire.
     * </p>
     * 
     * <p>
     * <b>This method should not be invoked by client code.</b>
     * </p>
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * <p>
     * Set the previous time at which the <code>SimpleTrigger</code> fired.
     * </p>
     * 
     * <p>
     * <b>This method should not be invoked by client code.</b>
     * </p>
     */
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    /**
     * <p>
     * Returns the next time at which the <code>SimpleTrigger</code> will
     * fire, after the given time. If the trigger will not fire after the given
     * time, <code>null</code> will be returned.
     * </p>
     */
    public Date getFireTimeAfter(Date afterTime) {
        if (complete) {
            return null;
        }

        if ((timesTriggered > repeatCount)
                && (repeatCount != REPEAT_INDEFINITELY)) {
            return null;
        }

        if (afterTime == null) {
            afterTime = new Date();
        }

        if (repeatCount == 0 && afterTime.compareTo(getStartTime()) >= 0) {
            return null;
        }

        long startMillis = getStartTime().getTime();
        long afterMillis = afterTime.getTime();
        long endMillis = (getEndTime() == null) ? Long.MAX_VALUE : getEndTime()
                .getTime();

        if (endMillis <= afterMillis) {
            return null;
        }

        if (afterMillis < startMillis) {
            return new Date(startMillis);
        }

        long numberOfTimesExecuted = ((afterMillis - startMillis) / repeatInterval) + 1;

        if ((numberOfTimesExecuted > repeatCount) && 
            (repeatCount != REPEAT_INDEFINITELY)) {
            return null;
        }

        Date time = new Date(startMillis + (numberOfTimesExecuted * repeatInterval));

        if (endMillis <= time.getTime()) {
            return null;
        }

        return time;
    }

    /**
     * <p>
     * Returns the last time at which the <code>SimpleTrigger</code> will
     * fire, before the given time. If the trigger will not fire before the
     * given time, <code>null</code> will be returned.
     * </p>
     */
    public Date getFireTimeBefore(Date end) {
        if (end.getTime() < getStartTime().getTime()) {
            return null;
        }

        int numFires = computeNumTimesFiredBetween(getStartTime(), end);

        return new Date(getStartTime().getTime() + (numFires * repeatInterval));
    }

    public int computeNumTimesFiredBetween(Date start, Date end) {

        if(repeatInterval < 1) {
            return 0;
        }
        
        long time = end.getTime() - start.getTime();

        return (int) (time / repeatInterval);
    }

    /**
     * <p>
     * Returns the final time at which the <code>SimpleTrigger</code> will
     * fire, if repeatCount is REPEAT_INDEFINITELY, null will be returned.
     * </p>
     * 
     * <p>
     * Note that the return time may be in the past.
     * </p>
     */
    public Date getFinalFireTime() {
        if (repeatCount == 0) {
            return startTime;
        }

        if (repeatCount == REPEAT_INDEFINITELY) {
            return (getEndTime() == null) ? null : getFireTimeBefore(getEndTime()); 
        }

        long lastTrigger = startTime.getTime() + (repeatCount * repeatInterval);

        if ((getEndTime() == null) || (lastTrigger < getEndTime().getTime())) { 
            return new Date(lastTrigger);
        } else {
            return getFireTimeBefore(getEndTime());
        }
    }

    /**
     * <p>
     * Determines whether or not the <code>SimpleTrigger</code> will occur
     * again.
     * </p>
     */
    public boolean mayFireAgain() {
        return (getNextFireTime() != null);
    }

    /**
     * <p>
     * Validates whether the properties of the <code>JobDetail</code> are
     * valid for submission into a <code>Scheduler</code>.
     * 
     * @throws IllegalStateException
     *           if a required property (such as Name, Group, Class) is not
     *           set.
     */
    public void validate() throws SchedulerException {
        super.validate();

        if (repeatCount != 0 && repeatInterval < 1) {
            throw new SchedulerException("Repeat Interval cannot be zero.",
                    SchedulerException.ERR_CLIENT_ERROR);
        }
    }

    /**
     * Used by extensions of SimpleTrigger to imply that there are additional 
     * properties, specifically so that extensions can choose whether to be 
     * stored as a serialized blob, or as a flattened SimpleTrigger table. 
     */
    public boolean hasAdditionalProperties() {
        return false;
    }

}
