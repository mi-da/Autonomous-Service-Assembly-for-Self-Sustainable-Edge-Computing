# execute all lines: ctrl + shift + enter

library(ggplot2)

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

colors <- c("global energy" = "#EEA236", "local energy"="gray64", "residual life" = "#5CB85C", "energy balance"="deeppink3", "random" = "#46B8DA", "QoS-aware"="#FF0000")

g = ggplot(data=local, aes(x=overall_x, col = group)) + 
  
  geom_line(aes(y = overall_y, color = "overall",linetype=b), size = 2, linetype="solid") +
  geom_line(aes(y = res_y, color = "residual life"), size = 2, linetype="dashed") +
  geom_line(aes(y = random_y, color = "random"), size = 2, linetype="dotted") +
  geom_line(aes(y = local_y, color = "local"), size = 2, linetype="dotdash") +
  geom_line(aes(y = energyAware_y, color = "energy balance"), size = 2, linetype="longdash") +
  geom_line(aes(y = qos_y, color = "qos"), size = 2, linetype="twodash")  +
  ylim(0, 1.0) +
  
  labs(x = "Learning cycle",
       y = "Instantaneous infrastructure availability") +

  scale_color_manual(values = colors)

  g+theme(panel.background = element_rect(fill="white"),
          panel.grid.minor.y = element_line(size=2),
          panel.grid.major = element_line(colour = "gray"),
          plot.background = element_rect(fill="white"),
          axis.text=element_text(size=30),
          axis.title=element_text(size=35),
          legend.position = "bottom",
          legend.text = element_text(size = 20) ) + 
    
    guides(colour = guide_legend(nrow = 1))
                                               

