/**
 * Copyright (c) 2004 - 2005
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclim.plugin.jdt.command.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.eclim.command.OutputFilter;

import org.eclim.util.file.Position;

import org.eclim.util.vim.VimUtils;

/**
 * Output filter for impl results.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ImplFilter
  implements OutputFilter
{
  private static final String NOT_FOUND_HEADER =
    "// The following types were not found, either because they were not\n" +
    "// imported or they were not found in the classpath.";

  /**
   * {@inheritDoc}
   */
  public String filter (Object _result)
  {
    if(_result instanceof ImplResult){
      ImplResult result = (ImplResult)_result;

      StringBuffer buffer = new StringBuffer();
      buffer.append(result.getType());

      List results = result.getSuperTypes();
      if(results != null){
        List notFound = new ArrayList();
        for(Iterator ii = results.iterator(); ii.hasNext();){
          ImplType type = (ImplType)ii.next();
          if(!type.getExists()){
            notFound.add(type);
          }

          if(type.getMethods() == null || type.getMethods().length == 0){
            continue;
          }

          if(buffer.length() > 0){
            buffer.append("\n\n");
          }

          buffer.append("package ").append(type.getPackage()).append(";\n");
          buffer.append(type.getSignature()).append(" {\n");
          ImplMethod[] methods = type.getMethods();
          if(methods != null){
            for(int jj = 0; jj < methods.length; jj++){
              if(jj > 0){
                buffer.append('\n');
              }
              String signature = methods[jj].getSignature();
              if(methods[jj].isImplemented()){
                signature = "//" + StringUtils.replace(signature, "\t", "\t//");
              }
              signature = StringUtils.replace(signature, "\n", "\n\t");
              buffer.append('\t').append(signature);
            }
          }
          buffer.append("\n}");
        }

        // print out those types that were not found.
        if(notFound.size() > 0){
          buffer.append("\n\n");
          buffer.append(NOT_FOUND_HEADER);
          for(Iterator ii = notFound.iterator(); ii.hasNext();){
            buffer.append("\n\t// ").append(((ImplType)ii.next()).getSignature());
          }
        }
      }
      return buffer.toString();
    }

    //Position position = (Position)_result;
    return "";
  }
}
