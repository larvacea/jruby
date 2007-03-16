/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
 * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby;

import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;

import org.rej.Registers;

/**
 *
 * @author  amoore
 */
public class RubyMatchData extends RubyObject {
    char[] str;
    Registers regs;
    
    public RubyMatchData(Ruby runtime) {
        super(runtime, runtime.getClass("MatchData"));
    }

    public static RubyClass createMatchDataClass(Ruby runtime) {
        // TODO: Is NOT_ALLOCATABLE_ALLOCATOR ok here, since you can't actually instanriate MatchData directly?
        RubyClass matchDataClass = runtime.defineClass("MatchData", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
        runtime.defineGlobalConstant("MatchingData", matchDataClass);

        CallbackFactory callbackFactory = runtime.callbackFactory(RubyMatchData.class);
        /*
        matchDataClass.defineFastMethod("captures", callbackFactory.getFastMethod("captures"));
        matchDataClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
        */
        matchDataClass.defineFastMethod("size", callbackFactory.getFastMethod("size"));
        matchDataClass.defineFastMethod("length", callbackFactory.getFastMethod("size"));
        matchDataClass.defineFastMethod("offset", callbackFactory.getFastMethod("offset", RubyKernel.IRUBY_OBJECT));
        /*
        matchDataClass.defineFastMethod("begin", callbackFactory.getFastMethod("begin", RubyFixnum.class));
        matchDataClass.defineFastMethod("end", callbackFactory.getFastMethod("end", RubyFixnum.class));
        matchDataClass.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
        matchDataClass.defineFastMethod("[]", callbackFactory.getFastOptMethod("aref"));
        */
        matchDataClass.defineFastMethod("pre_match", callbackFactory.getFastMethod("pre_match"));
        matchDataClass.defineFastMethod("post_match", callbackFactory.getFastMethod("post_match"));
        /*
        matchDataClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
        matchDataClass.defineFastMethod("string", callbackFactory.getFastMethod("string"));
        */
        matchDataClass.getMetaClass().undefineMethod("new");

        return matchDataClass;
    }

    /** match_size
     *
     */
    public IRubyObject size() {
        return getRuntime().newFixnum(regs.num_regs);
    }

    /** match_offset
     *
     */
    public IRubyObject offset(IRubyObject index) {
        int i = RubyNumeric.num2int(index);

        if(i < 0 || regs.num_regs <= i) {
            throw getRuntime().newIndexError("index " + i + " out of matches");
        }

        if(regs.beg[i] < 0) {
            return getRuntime().newArray(getRuntime().getNil(),getRuntime().getNil());
        }
        return getRuntime().newArray(getRuntime().newFixnum(regs.beg[i]),getRuntime().newFixnum(regs.end[i]));
    }

    /** match_pre_match
     *
     */
    public IRubyObject pre_match() {
        if(regs.beg[0] == -1) {
            return getRuntime().getNil();
        }
        RubyString str_ = RubyString.newString(getRuntime(),new String(str,0,regs.beg[0]));
        if(isTaint()) {
            str_.taint();
        }
        return str_;
    }

    /** match_post_match
     *
     */
    public IRubyObject post_match() {
        if(regs.beg[0] == -1) {
            return getRuntime().getNil();
        }
        RubyString str_ = RubyString.newString(getRuntime(),new String(str,regs.end[0],str.length-regs.end[0]));
        if(isTaint()) {
            str_.taint();
        }
        return str_;
    }
}
