# execute all lines: ctrl + shift + enter

library(ggplot2)
library(ggforce)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))

overall <- read.table("exp_assembly_overall_energy_template.txt", 
                      header = F,
                      sep = " ",
                      col.names= c("overallx", "overally")
)

random <- read.table("exp_assembly_random.txt", 
                     header = F,
                     sep = " ",
                     col.names= c("randomx", "randomy")
)

res <- read.table("exp_assembly_residual_life_template.txt", 
                  header = F,
                  sep = " ",
                  col.names= c("resx","resy")
)

local <- read.table("exp_assembly_local_energy_template.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)

learning <- read.table("exp_assembly_fair_energyBP.txt", 
                       header = F,
                       sep = " ",
                       col.names= c("enx","eny")
)

qos <- read.table("exp_assembly_QoS.txt", 
                  header = F,
                  sep = " ",
                  col.names= c("qosx","qosy")
)


overall_x <- overall$overallx
overall_y <- overall$overally

random_y <- random$randomy
res_y <- res$resy
local_y <- local$locy
energyAware_y <- learning$eny
qos_y <- qos$qosy

head(overall)

sq<-function(x){
  x^4
}
#inverse square function (square root)
isq<-function(x){
  print(paste("isq",x))  #debug statement
  x<-ifelse(x<0, 0, x)
  x^(1/4)
}


colors <- c("overall" = "#EEA236", "residual life" = "#5CB85C", "random" = "#46B8DA", "local"="gray64", "energy balance"="deeppink3", "qos"="#FF0000")

g = ggplot(data=local, aes(x=overall_x, col = group)) + 
  geom_line(aes(y = overall_y, color = "overall",linetype=b), size = 2, linetype="solid") +
  geom_line(aes(y = res_y, color = "residual life"), size = 2, linetype="dashed") +
  geom_line(aes(y = random_y, color = "random"), size = 2, linetype="dotted") +
  geom_line(aes(y = local_y, color = "local"), size = 2, linetype="dotdash") +
  geom_line(aes(y = energyAware_y, color = "energy balance"), size = 2, linetype="longdash") +
  geom_line(aes(y = qos_y, color = "qos"), size = 2, linetype="twodash")  +
  labs(x = "Hours",
       y = "Instantaneous infrastructure availability") +
  scale_color_manual(values = colors) +
  scale_y_continuous(trans = scales::trans_new("sq",sq,isq),limits = c(0,1.00), breaks = c(0.0,0.5,0.6,0.7,0.8,0.9,1.0) )

  g+ theme(panel.background = element_rect(fill="white"),
          panel.grid.minor.y = element_line(size=2),
          panel.grid.major = element_line(colour = "gray"),
          plot.background = element_rect(fill="white"),
          axis.text=element_text(size=30),
          axis.title=element_text(size=35),
          legend.position = "none")
  